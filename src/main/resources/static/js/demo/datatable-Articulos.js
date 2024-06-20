document.addEventListener("DOMContentLoaded", function() {
    // Fetch artículos
    fetch("http://localhost:9090/api/v1/articulos")
        .then(response => response.json())
        .then(data => {
            const tableBody = document.querySelector("#articulos-table tbody");
            data.forEach(articulo => {
                //articulo.loteOptimoArticulo = articulo.loteOptimoArticulo;
                const row = document.createElement("tr");
                row.innerHTML = `
                    <td>${articulo.id}</td>
                    <td>${articulo.nombreArticulo}</td>
                    <td>${articulo.cantidadArticulo}</td>
                    <td>${articulo.modeloInventario}</td>
                    <td>${articulo.proveedorPredeterminado ? articulo.proveedorPredeterminado.nombreProveedor : 'No asignado'}</td>
                    <td>${articulo.demandaAnualArticulo}</td>
                    <td>${articulo.tiempoRevisionArticulo}</td>
                    <td>${articulo.loteOptimoArticulo}</td>
                    <td>${articulo.puntoPedidoArticulo}</td>
                    <td>${articulo.stockSeguridadArticulo}</td>
                    <td>${articulo.cgiArticulo}</td>
                    <td>
                        <div style="align-content: center">
                            <a href="#" class="btn btn-warning btn-circle btn-sm btn-modificar-articulo" id="modificar-articulo" data-id="${articulo.id}">
                                <i class="fas fa-edit"></i>
                            </a>
                            <a href="#" class="btn btn-danger btn-circle btn-sm borrar-articulo" data-id="${articulo.id}">
                                <i class="fas fa-trash"></i>
                            </a>
                        </div>
                    </td>
                `;
                tableBody.appendChild(row);
            });
        })
        .catch(error => {
            console.error("Error al obtener los artículos:", error);
        });

//Trae los proveedores cuando se crea
    function cargarProveedores() {
        fetch("http://localhost:9090/api/v1/proveedores")
            .then(response => response.json())
            .then(data => {
                const proveedorSelect = document.querySelector("#proveedor");
                proveedorSelect.innerHTML = '<option value="">Seleccione un proveedor</option>';
                data.forEach(proveedor => {
                    const option = document.createElement("option");
                    option.textContent = proveedor.nombreProveedor;
                    option.value = proveedor.id;
                    proveedorSelect.appendChild(option);
                });
            })
            .catch(error => {
                console.error('Error al obtener la lista de proveedores:', error);
            });
    }

    // Mostrar el modal y cargar proveedores al hacer clic en el botón para crear un artículo
    $('#crearArticuloModal').on('show.bs.modal', function () {
        cargarProveedores();
    });

    $(document).ready(function() {
        $('#dataTable').DataTable();

        // Mostrar/Ocultar campo de tiempo entre pedidos según el modelo seleccionado
        $('#modelo').change(function() {
            var modeloSeleccionado = $(this).val();
            if (modeloSeleccionado === 'MODELO_INTERVALO_FIJO') {
                $('#campoTiempoEntrePedidos').show();
            } else {
                $('#campoTiempoEntrePedidos').hide();
            }
        });

        // Mostrar/Ocultar detalles del proveedor según el proveedor seleccionado
        $('#proveedor').change(function() {
            var proveedorSeleccionado = $(this).val();
            if (proveedorSeleccionado) {
                $('#proveedorDetalles').show();
            } else {
                $('#proveedorDetalles').hide();
            }
        });

        // Enviar el formulario de creación de artículo
        $('#guardarArticulo').click(function() {
            var modeloSeleccionado = $('#modelo').val();
            var tiempoEntrePedidos = modeloSeleccionado === 'MODELO_INTERVALO_FIJO' ? $('#tiempoEntrePedidos').val() : null;

            var proveedorId = parseInt($('#proveedor').val());
            var proveedorNombre = $('#proveedor option:selected').text();

            var formData = {
                articulo: {
                    nombreArticulo: $('#nombre').val(),
                    cantidadArticulo: parseInt($('#cantidad').val()), // Convertir a número
                    costoAlmacenamientoArticulo: null,
                    costoPedidoArticulo: null,
                    stockSeguridadArticulo: null,
                    cgiArticulo: null,
                    modeloInventario: modeloSeleccionado,
                    demandaAnualArticulo: parseInt($('#demandaanual').val()), // Convertir a número
                    loteOptimoArticulo: null,
                    puntoPedidoArticulo: null,
                    cantidadMaximaArticulo: null,
                    tiempoRevisionArticulo: tiempoEntrePedidos ? parseInt(tiempoEntrePedidos) : null, // Convertir a número si no es nulo
                    proveedorPredeterminado: {
                        id: proveedorId,
                        nombreProveedor: proveedorNombre
                    }
                },
                proveedorArticulo: {
                    tiempoDemoraArticulo: parseFloat($('#tiempoDemora').val()), // Convertir a número
                    precioArticuloProveedor: parseFloat($('#precio').val()), // Convertir a número
                    costoPedidoArticuloProveedor: parseFloat($('#costoPedido').val()), // Convertir a número
                    costoAlmacenamientoArticuloProveedor: parseFloat($('#costoAlmacenamiento').val()), // Convertir a número
                    proveedor: {
                        id: proveedorId,
                        nombreProveedor: proveedorNombre
                    }
                }
            };

            console.log('Enviando datos:', JSON.stringify(formData)); // Depuración

            $.ajax({
                type: 'POST',
                url: 'http://localhost:9090/api/v1/articulos/crear',
                contentType: 'application/json',
                data: JSON.stringify(formData),
                success: function(response) {
                    $('#crearArticuloModal').modal('hide');
                    alert('Artículo creado exitosamente');

                    // Agregar el nuevo artículo a la tabla
                    const tableBody = document.querySelector("#articulos-table tbody");
                    const row = document.createElement("tr");
                    row.innerHTML = `
                    <td>${response.id}</td>
                    <td>${response.nombreArticulo}</td>
                    <td>${response.cantidadArticulo}</td>
                    <td>${response.modeloInventario}</td>
                    <td>${response.proveedorPredeterminado ? response.proveedorPredeterminado.nombreProveedor : 'No asignado'}</td>
                    <td>${response.demandaAnualArticulo}</td>
                    <td>${response.tiempoRevisionArticulo}</td>
                    <td>${response.loteOptimoArticulo}</td>
                    <td>${response.puntoPedidoArticulo}</td>
                    <td>${response.stockSeguridadArticulo}</td>
                    <td>${response.cgiArticulo}</td>
                    <td>
                        <div style="align-content: center">
                            <a href="#" class="btn btn-warning btn-circle btn-sm btn-modificar-articulo" data-id="${response.id}">
                                <i class="fas fa-edit"></i>
                            </a>
                            <a href="#" class="btn btn-danger btn-circle btn-sm borrar-articulo" data-id="${response.id}">
                                <i class="fas fa-trash"></i>
                            </a>
                        </div>
                    </td>
                `;
                    tableBody.appendChild(row);

                    // Limpia el formulario después de enviar
                    $('#crearArticuloForm')[0].reset();
                },
                error: function(xhr, status, error) {
                    console.error('Error al crear el artículo:', xhr.responseText);
                    alert('Error al crear el artículo: ' + (xhr.responseJSON && xhr.responseJSON.message ? xhr.responseJSON.message : 'Desconocido'));
                }
            });
        });



        // Manejar clic en el botón de eliminación
    $(document).on('click', '.borrar-articulo', function(event) {
        event.preventDefault();
        const articuloId = $(this).data('id');

        if (confirm('¿Está seguro de que desea eliminar este artículo?')) {
            $.ajax({
                type: 'GET',
                url: `http://localhost:9090/api/v1/articulos/${articuloId}/control-orden-compra`,
                success: function(response) {
                    if (response) {
                        alert('El artículo tiene una orden de compra activa y no puede ser eliminado');
                    } else {
                        $.ajax({
                            type: 'DELETE',
                            url: `http://localhost:9090/api/v1/articulos/${articuloId}`,
                            success: function() {
                                alert('Artículo eliminado exitosamente');
                                location.reload();  // Actualizar la tabla para reflejar la eliminación
                            },
                            error: function(error) {
                                alert('Error al eliminar el artículo');
                            }
                        });
                    }
                },
                error: function(error) {
                    alert('Error al verificar la orden de compra activa');
                }
            });
        }
    });
});


//PARA MODIFICACION DE ARTICULOS


//fetch UN solo articulo
/*async function obtenerArticuloSeleccionado(idArticulo) {
    try {
        const response = await fetch(`http://localhost:9090/api/v1/articulos/${idArticulo}`);
        if (!response.ok) {
            throw new Error('No se pudo obtener el artículo');
        }
        const articulo = await response.json();
        return articulo;
    } catch (error) {
        console.error("Error al obtener el artículo:", error);
        throw error; // Puedes relanzar el error para manejarlo en el contexto que llame a esta función
    }
}*/


    $(document).on('click', '.btn-modificar-articulo', function (event) {
        event.preventDefault();
        var articuloId = $(this).data('id');
        $('#modificarArticuloModal').modal('show');
        const modelosInventario = ['MODELO_INTERVALO_FIJO', 'MODELO_LOTE_FIJO'];

        // Obtener la información del artículo
        $.ajax({
            type: 'GET',
            url: `http://localhost:9090/api/v1/articulos/${articuloId}`,
            success: function(articulo) {
                console.log('Artículo obtenido:', articulo);
                // Rellena el formulario con la información del artículo
                $('#nombreParaModificar').val(articulo.nombreArticulo);
                const modeloSelect = $('#modeloParaModificar');
                modeloSelect.empty();
                modelosInventario.forEach(function (modelo) {
                    const option = $('<option>').text(modelo).attr('value', modelo);
                    if (modelo === articulo.modeloInventario) {
                        option.attr('selected', 'selected');
                    }
                    modeloSelect.append(option);
                });
                // Mostrar u ocultar el campo de tiempo entre pedidos basado en el modelo de inventario
                toggleTiempoEntrePedidosModificacion(articulo.modeloInventario);

                $.ajax({
                    type: 'GET',
                    url: `http://localhost:9090/api/v1/proveedoresarticulos/findProveedoresByArticulo/${articuloId}`,
                    success: function(proveedoresarticulos) {
                        console.log('Proveedores obtenidos:', proveedoresarticulos);
                        const proveedorSelect = $('#proveedorParaModificar');
                        proveedorSelect.empty();
                        const defaultOption = document.createElement("option");
                        if (articulo.proveedorPredeterminado === null) {
                            defaultOption.textContent = "Seleccione un proveedor";
                        } else {
                            defaultOption.textContent = articulo.proveedorPredeterminado.proveedor;
                        }
                        proveedorSelect.append(defaultOption);
                        proveedoresarticulos.forEach(function(proveedorarticulo) {
                            const proveedor = proveedorarticulo.proveedor;
                            const option = $('<option>').text(proveedor.nombreProveedor).attr('value', proveedor.id);
                            if (articulo.proveedorPredeterminado && proveedor.id === articulo.proveedorPredeterminado.id) {
                                option.attr('selected', 'selected');
                            }
                            proveedorSelect.append(option);
                        });
                    },
                    error: function(error) {
                        console.error('Error al obtener la lista de proveedores:', error);
                    }
                });

            },
            error: function(error) {
                console.error('Error al obtener la información del artículo:', error);
            }
        });

        $('#modeloParaModificar').on('change', function() {
            toggleTiempoEntrePedidosModificacion($(this).val());
        });

        $('#guardarArticuloModificado').off('click').on('click', function() {
            console.log('ID DEL ARTICULO:', articuloId);
            var formData = {
                id: articuloId,
                nombreArticulo: $('#nombreParaModificar').val(),
                modeloInventario: $('#modeloParaModificar').val(),
                proveedorPredeterminado: {
                    id: $('#proveedorParaModificar').val()
                },
               // tiempoEntrePedidosModificacion: $('#tiempoEntrePedidosModificacion').val()
            };

            // Verificar el valor seleccionado del proveedor
            console.log('ID del proveedor seleccionado:', $('#proveedorParaModificar').val());
            console.log('Datos enviados:', formData);

            // Realizar una solicitud PATCH al servidor para modificar el artículo
            $.ajax({
                type: 'PATCH',
                url: `http://localhost:9090/api/v1/articulos/modificar/${articuloId}`,
                contentType: 'application/json',
                data: JSON.stringify(formData),
                success: function(response) {
                    console.log('Respuesta del servidor:', response);
                    $('#modificarArticuloModal').modal('hide'); // Cierra el modal de modificación
                    alert('Artículo modificado exitosamente');
                    location.reload();
                },
                error: function(error) {
                    console.error('Error al modificar el artículo:', error);
                    alert('Error al modificar el artículo');
                }
            });
        });
    });

    function toggleTiempoEntrePedidosModificacion(modelo) {
        if (modelo === 'MODELO_INTERVALO_FIJO') {
            $('#tiempoEntrePedidosModificacionGroup').show();
        } else {
            $('#tiempoEntrePedidosModificacionGroup').hide();
        }
    }
})

