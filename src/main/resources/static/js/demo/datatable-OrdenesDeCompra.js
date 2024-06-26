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
                        ${ordenesdecompras.estadoOrdenCompra === 'PENDIENTE' ? `
                        <a href="#" class="btn btn-warning btn-circle btn-sm btn-modificar-OC" data-id="${ordenesdecompras.id}">
                            <i class="fas fa-edit"></i>
                        </a>` : ''}
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

            // LLAMA EL METODO PARA EDITAR LA ORDEN
            tableBody.addEventListener('click', function(event) {
                if (event.target.classList.contains('btn-modificar-OC') || event.target.parentElement.classList.contains('btn-modificar-OC')) {
                    const ordenCompraId = event.target.closest('.btn-modificar-OC').getAttribute('data-id');
                    abrirEditarOrdenCompraModal(ordenCompraId);
                }
            });
        })
        .catch(error => {
            console.error("Error al obtener las órdenes de compra:", error);
        });
});

// ABRIR EL MODAL DE MODIFICACION
function abrirEditarOrdenCompraModal(ordenCompraId) {
    console.log(`Abriendo modal para la orden de compra con ID: ${ordenCompraId}`);
    fetch(`http://localhost:9090/api/v1/ordenescompras/${ordenCompraId}`)
        .then(response => response.json())
        .then(data => {
            console.log('Datos de la orden de compra obtenidos:', data);
            // Mostrar el modal de edición
            $('#editarOrdenDeCompraModal').modal('show');

            // Cargar el proveedor actual
            $('#editarproveedor').val(data.proveedor ? data.proveedor.id : '');

            // Cargar proveedores disponibles para el artículo asociado
            cargarproveedores(data.ordenCompraDetalles[0].articulo.id, 'editar');

            // Cargar la cantidad a pedir
            $('#editarcantidadorden').val(data.ordenCompraDetalles[0].cantidadAComprar);

            // Evento para el botón de guardar en el modal
            $('#editarOrdenDeCompraModal .btn-primary').off('click').on('click', function() {
                editarOrdenDeCompra(data.id);
            });
        })
        .catch(error => {
            console.error('Error al cargar la orden de compra para editar:', error);
        });
}

// Función para cargar proveedores para un artículo específico
function cargarproveedores(articuloId, contexto) {
    $.ajax({
        type: 'GET',
        url: `http://localhost:9090/api/v1/proveedoresarticulos/findProveedoresByArticulo/${articuloId}`,
        success: function(proveedoresarticulos) {
            console.log('Proveedores obtenidos:', proveedoresarticulos);

            const proveedorSelect = contexto === 'editar' ? $('#editarproveedor') : $('#proveedor');
            proveedorSelect.empty(); // Clear previous options

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
    console.log("Modal para crear orden de compra mostrado");
});

$('#crearOrdenDeCompraModal .btn-primary').on('click', guardarOrdenDeCompra);

// Función para guardar la orden de compra
function guardarOrdenDeCompra() {
    console.log("Función guardarOrdenDeCompra llamada");
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
                // Si no hay una orden activa, procedemos a crear la nueva orden
                const nuevaOrden = {
                    fechaOrdenCompra: fechaOrdenCompra,
                    estadoOrdenCompra: estadoOrdenCompra,
                    proveedor: {
                        id: proveedorId,
                        nombreProveedor: proveedorNombre
                    },
                    ordenCompraDetalles: [{
                        cantidadAComprar: cantidadAComprar,
                        articulo: {
                            id: articuloId,
                            nombreArticulo: articuloNombre
                        }
                    }]
                };
                console.log("Nueva orden de compra a enviar:", nuevaOrden);

                fetch("http://localhost:9090/api/v1/ordenescompras", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify(nuevaOrden),
                })
                    .then(response => response.json())
                    .then(data => {
                        $('#crearOrdenDeCompraModal').modal('hide');
                        location.reload();
                    })
                    .catch(error => {
                        console.error("Error al crear la orden de compra:", error);
                    });
            }
        },
        error: function(error) {
            console.error('Error al verificar si el artículo tiene una orden activa:', error);
        }
    });
}

// Función para editar la orden de compra
function editarOrdenDeCompra(ordenCompraId) {
    console.log("Función editarOrdenDeCompra llamada para ID:", ordenCompraId);
    const proveedorId = $('#editarproveedor').val();
    const proveedorNombre = $('#editarproveedor option:selected').text();
    const cantidadAComprar = $('#editarcantidadorden').val();
    const articuloId = $('#editararticulo').val();
    const articuloNombre = $('#editararticulo option:selected').text();

    const ordenEditada = {
        id: ordenCompraId,
        proveedor: {
            id: proveedorId,
            nombreProveedor: proveedorNombre
        },
        ordenCompraDetalles: [{
            cantidadAComprar: cantidadAComprar,
            articulo: {
                id: articuloId,
                nombreArticulo: articuloNombre
            }
        }]
    };
    console.log("Orden de compra editada a enviar:", ordenEditada);

    fetch(`http://localhost:9090/api/v1/ordenescompras/${ordenCompraId}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(ordenEditada),
    })
        .then(response => response.json())
        .then(data => {
            $('#editarOrdenDeCompraModal').modal('hide');
            location.reload();
        })
        .catch(error => {
            console.error("Error al editar la orden de compra:", error);
        });
}

// Cargar artículos disponibles
function cargararticulos() {
    $.ajax({
        type: 'GET',
        url: 'http://localhost:9090/api/v1/articulos',
        success: function(articulos) {
            console.log('Artículos obtenidos:', articulos);
            const articuloSelect = $('#articulo');
            articuloSelect.empty(); // Clear previous options

            articuloSelect.append('<option value="">Seleccione un Artículo</option>');

            articulos.forEach(function(articulo) {
                const option = $('<option>').text(articulo.nombreArticulo).attr('value', articulo.id);
                articuloSelect.append(option);
            });

            // Evento para cargar proveedores al cambiar de artículo
            articuloSelect.off('change').on('change', function() {
                const articuloId = $(this).val();
                if (articuloId) {
                    cargarproveedores(articuloId, 'crear');
                    cargarproveedorpredeterminado(articuloId);
                }
            });
        },
        error: function(error) {
            console.error('Error al obtener la lista de artículos:', error);
        }
    });
}
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
