//TRAE LAS OC Y LLAMA A LOS METODOS DE CAMBIO DE ESTADO
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
                <td>${ordenesdecompras.proveedor ? ordenesdecompras.proveedor.nombreProveedor : 'No asignado'}</td>
                <td>
                    <button class="btn btn-primary btn-sm btn-detalle" data-id="${ordenesdecompras.id}">
                        Detalle
                    </button>
                </td>
                <td>
                    <div style="text-align: center">
                        <a href="#" class="btn btn-success btn-circle btn-sm btn-finalizar" data-id="${ordenesdecompras.id}">
                                        <i class="fas fa-check"></i>
                        </a>
                        <a href="#" class="btn btn-info btn-circle btn-sm btn-accion btn-confirmar" data-id="${ordenesdecompras.id}">
                                        <i class="fa fa-arrow-circle-right" aria-hidden="true"></i>
                        </a>
                        <a href="#" class="btn btn-danger btn-circle btn-sm btn-accion btn-cancelar" data-id="${ordenesdecompras.id}">
                                        <i class="fa fa-times" aria-hidden="true"></i>
                        </a>
                    </div>
                </td>
            `;
                tableBody.appendChild(row);
            });

            // LLAMA AL METODO PARA IR AL DETALLE DE LAS OC
            tableBody.addEventListener('click', function(event) {
                if (event.target.classList.contains('btn-detalle')) {
                    const ordenCompraId = event.target.getAttribute('data-id');
                    // Redirigir a OrdenCompraDetalle.html con el ID de la orden de compra
                    window.location.href = `OrdenCompraDetalle.html?id=${ordenCompraId}`;
                }
            });

            // LLAMA EL METODO PARA CONFIRMAR LA ORDEN (PENDIENTE --> EN CURSO)
            tableBody.addEventListener('click', function(event) {
                if (event.target.classList.contains('btn-confirmar') || event.target.parentElement.classList.contains('btn-confirmar')) {
                    const ordenCompraId = event.target.closest('.btn-confirmar').getAttribute('data-id');
                    confirmarOrdenCompra(ordenCompraId);
                }
            });

            // LLAMA EL METODO PARA FINALIZAR LA ORDEN (EN CURSO --> FINALIZADA)
            tableBody.addEventListener('click', function(event) {
                if (event.target.classList.contains('btn-finalizar') || event.target.parentElement.classList.contains('btn-finalizar')) {
                    const ordenCompraId = event.target.closest('.btn-finalizar').getAttribute('data-id');
                    finalizarOrdenCompra(ordenCompraId);
                }
            });

            // LLAMA EL METODO PARA CANCELAR LA ORDEN ( EN CURSO - PENDIENTE --> CANCELADA)
            tableBody.addEventListener('click', function(event) {
                if (event.target.classList.contains('btn-cancelar') || event.target.parentElement.classList.contains('btn-cancelar')) {
                    const ordenCompraId = event.target.closest('.btn-cancelar').getAttribute('data-id');
                    cancelarOrdenCompra(ordenCompraId);
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
    const cantidadAComprar = $('#cantidadorden').val();
    const articuloId = $('#articulo').val();
    const articuloNombre = $('#articulo option:selected').text();


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

//MODIFICACION DE ESTADO MANUAL
//MODIFICACION DE ESTADO MANUAL
//MODIFICACION DE ESTADO MANUAL

// FUNCION PARA CONFIRMAR (PENDIENTE --> EN CURSO)
function confirmarOrdenCompra(ordenCompraId) {
    fetch(`http://localhost:9090/api/v1/ordenescompras/confirmar/${ordenCompraId}`, {
        method: 'PUT'
    })
        .then(response => {
            if (response.ok) {
                alert('Orden de compra confirmada exitosamente');
                location.reload(); // Recargar la página para reflejar los cambios
            } else {
                alert('Error al confirmar la orden de compra');
            }
        })
        .catch(error => {
            console.error('Error al confirmar la orden de compra:', error);
        });
}

function finalizarOrdenCompra(ordenCompraId) {
    fetch(`http://localhost:9090/api/v1/ordenescompras/finalizar/${ordenCompraId}`, {
        method: 'PUT'
    })
        .then(response => {
            if (response.ok) {
                alert('Orden de compra Finalizada exitosamente');
                location.reload(); // Recargar la página para reflejar los cambios
            } else {
                alert('Error al Finalizar la orden de compra');
            }
        })
        .catch(error => {
            console.error('Error al Finalizar la orden de compra:', error);
        });
}

function cancelarOrdenCompra(ordenCompraId) {
    fetch(`http://localhost:9090/api/v1/ordenescompras/cancelar/${ordenCompraId}`, {
        method: 'PUT'
    })
        .then(response => {
            if (response.ok) {
                alert('Orden de compra Cancelada exitosamente');
                location.reload(); // Recargar la página para reflejar los cambios
            } else {
                alert('Error al Cancelada la orden de compra');
            }
        })
        .catch(error => {
            console.error('Error al Cancelada la orden de compra:', error);
        });
}