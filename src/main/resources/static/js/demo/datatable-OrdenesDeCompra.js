document.addEventListener("DOMContentLoaded", function() {
    fetch("http://localhost:9090/api/v1/ordenescompras")
        .then(response => response.json())
        .then(data => {
            const tableBody = document.querySelector("#ordenesdecompra-table tbody");
            data.forEach(ordenesdecompras => {
                const row = document.createElement("tr");

                // Obtener nombres de los artículos de los detalles de la orden de compra
                let nombresArticulos = ordenesdecompras.ordenCompraDetalles
                    .map(detalle => detalle.articulo ? detalle.articulo.nombreArticulo : 'No asignado')
                    .join(', ');

                row.innerHTML = `
                <td>${ordenesdecompras.id}</td>
                <td>${nombresArticulos}</td>
                <td>${new Date(ordenesdecompras.fechaOrdenCompra).toLocaleString()}</td>
                <td>${ordenesdecompras.estadoOrdenCompra}</td>
                <td>${ordenesdecompras.totalOrdenCompra}</td>
                <td>${ordenesdecompras.proveedor ? ordenesdecompras.proveedor.nombreProveedor : 'No asignado'}</td>
                <td>
                    <button class="btn btn-primary btn-sm btn-detalle" data-id="${ordenesdecompras.id}">
                        Detalle
                    </button>
                </td>
                <td>
                    <div style="text-align: center">
                        <a href="#" class="btn btn-success btn-circle btn-sm" data-id="${ordenesdecompras.id}">
                                        <i class="fas fa-check"></i>
                        </a>
                        <a href="#" class="btn btn-info btn-circle btn-sm" data-id="${ordenesdecompras.id}">
                                        <i class="fa fa-arrows-h" ></i>
                        </a>
                        
                    </div>
                </td>
            `;
                tableBody.appendChild(row);
            });

            // Agregar evento click al botón de detalle
            tableBody.addEventListener('click', function(event) {
                if (event.target.classList.contains('btn-detalle')) {
                    const ordenCompraId = event.target.getAttribute('data-id');
                    // Redirigir a OrdenCompraDetalle.html con el ID de la orden de compra
                    window.location.href = `OrdenCompraDetalle.html?id=${ordenCompraId}`;
                }
            });
        })
        .catch(error => {
            console.error("Error al obtener las órdenes de compra:", error);
        });
});

// Función para cargar artículos
function cargararticulos() {
    $.ajax({
        type: 'GET',
        url: 'http://localhost:9090/api/v1/articulos',
        success: function(articulos) {
            const articuloSelect = $('#articulo');
            articuloSelect.empty();
            articuloSelect.append('<option value="">Seleccione un Artículo</option>');
            articulos.forEach(function(articulo) {
                const option = $('<option>').text(articulo.nombreArticulo).attr('value', articulo.id);
                articuloSelect.append(option);
            });

            // Agregar un evento para detectar cambios en la selección del artículo
            articuloSelect.on('change', function() {
                const selectedArticuloId = $(this).val();
                if (selectedArticuloId) {
                    cargarproveedores(selectedArticuloId);
                    cargarproveedorpredeterminado(selectedArticuloId);
                } else {
                    const proveedorSelect = $('#proveedor');
                    proveedorSelect.empty();
                    proveedorSelect.append('<option value="">Seleccione un Proveedor</option>');
                    const proveedorpredeterminadoElement = $('#proveedorpredeterminado');
                    proveedorpredeterminadoElement.empty();
                }
            });
        },
        error: function(error) {
            console.error('Error al obtener la lista de artículos:', error);
        }
    });
}

// Función para cargar proveedores para un artículo específico
function cargarproveedores(articuloId) {
    $.ajax({
        type: 'GET',
        url: `http://localhost:9090/api/v1/proveedoresarticulos/findProveedoresByArticulo/${articuloId}`,
        success: function(proveedoresarticulos) {
            console.log('Proveedores obtenidos:', proveedoresarticulos);
            const proveedorSelect = $('#proveedor'); // Verificar el ID del select aquí
            proveedorSelect.empty(); // Limpiamos las opciones anteriores

            proveedorSelect.append('<option value="">Seleccione un Proveedor</option>');

            if (proveedoresarticulos && proveedoresarticulos.length > 0) {
                proveedoresarticulos.forEach(function(proveedorarticulo) {
                    const proveedor = proveedorarticulo.proveedor;
                    const option = $('<option>').text(proveedor.nombreProveedor).attr('value', proveedor.id);
                    proveedorSelect.append(option);
                });
            } else {
                console.log('No se encontraron proveedores para el artículo con ID:', articuloId);
            }
        },
        error: function(error) {
            console.error('Error al obtener la lista de proveedores:', error);
        }
    });
}

// Función para cargar el proveedor predeterminado para un artículo específico
function cargarproveedorpredeterminado(articuloId) {
    $.ajax({
        type: 'GET',
        url: `http://localhost:9090/api/v1/articulos/${articuloId}/proveedor-predeterminado`,
        success: function(data) {
            const proveedorpredeterminadoElement = $('#proveedorpredeterminado');
            proveedorpredeterminadoElement.empty();
            proveedorpredeterminadoElement.append(`<p>${data.nombreProveedor}</p>`);
        },
        error: function(error) {
            console.error('Error al obtener el proveedor predeterminado:', error);
        }
    });
}

// Mostrar el modal y cargar artículos al hacer clic en el botón para crear un artículo
$('#crearOrdenDeCompraModal').on('show.bs.modal', function () {
    cargararticulos();
});

// Función para guardar la orden de compra
function guardarOrdenDeCompra() {
    const fechaOrdenCompra = new Date().toISOString().split('T')[0];
    const estadoOrdenCompra = 'PENDIENTE';
    const proveedorId = $('#proveedor').val();
    const proveedorNombre = $('#proveedor option:selected').text();
    const totalOrdenCompra = $('#totalorden').val();
    const cantidadAComprar = $('#cantidadorden').val();
    const articuloId = $('#articulo').val();
    const articuloNombre = $('#articulo option:selected').text();

    if (!proveedorId || !totalOrdenCompra || !articuloId || !cantidadAComprar) {
        alert('Por favor, complete todos los campos requeridos.');
        return;
    }

    // ver si tiene orden
    $.ajax({
        type: 'GET',
        url: `http://localhost:9090/api/v1/ordenescompras/articuloconordenactiva?articuloId=${articuloId}`,
        success: function(response) {
            if (response) {
                alert('El artículo ya tiene una orden de compra activa.');
            } else {
                // If no active order exists, proceed to create the new order
                const ordenDeCompra = {
                    fechaOrdenCompra: fechaOrdenCompra,
                    estadoOrdenCompra: estadoOrdenCompra,
                    totalOrdenCompra: parseFloat(totalOrdenCompra),
                    ordenCompraDetalles: [{
                        cantidadAComprar: parseInt(cantidadAComprar), // Ensure quantity is an integer
                        articulo: {
                            id: parseInt(articuloId), // Ensure articuloId is an integer
                            nombreArticulo: articuloNombre
                        }
                    }],
                    proveedor: {
                        id: parseInt(proveedorId), // Ensure proveedorId is an integer
                        nombreProveedor: proveedorNombre
                    }
                };

                $.ajax({
                    type: 'POST',
                    url: 'http://localhost:9090/api/v1/ordenescompras',
                    contentType: 'application/json',
                    data: JSON.stringify(ordenDeCompra),
                    success: function(response) {
                        alert('Orden de Compra guardada con éxito!');
                        $('#crearOrdenDeCompraModal').modal('hide');
                        // Clear the form
                        $('#proveedor').val('');
                        $('#totalorden').val('');
                        $('#cantidadorden').val('');
                        $('#articulo').val('');
                    },
                    error: function(error) {
                        console.error('Error al guardar la Orden de Compra:', error);
                        alert('Error al guardar la Orden de Compra.');
                    }
                });
            }
        },
        error: function(error) {
            console.error('Error al verificar la orden de compra activa:', error);
            alert('Error al verificar la orden de compra activa.');
        }
    });


}

// guardar
$('#crearOrdenDeCompraModal .btn-primary').on('click', guardarOrdenDeCompra);

