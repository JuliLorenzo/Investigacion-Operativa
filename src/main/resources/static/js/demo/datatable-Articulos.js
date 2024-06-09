document.addEventListener("DOMContentLoaded", function() {
    // Fetch artículos
    fetch("http://localhost:9090/api/v1/articulos")
        .then(response => response.json())
        .then(data => {
            const tableBody = document.querySelector("#articulos-table tbody");
            data.forEach(articulo => {
                const row = document.createElement("tr");
                row.innerHTML = `
                    <td>${articulo.id}</td>
                    <td>${articulo.nombreArticulo}</td>
                    <td>${articulo.cantidadArticulo}</td>
                    <td>${articulo.proveedorPredeterminado ? articulo.proveedorPredeterminado.nombreProveedor : 'No asignado'}</td>
                    <td>
                        <div style="align-content: center">
                            <a href="#" class="btn btn-info btn-circle btn-sm" data-id="${articulo.id}">
                                <i class="fas fa-link"></i>
                            </a>
                            <a href="#" class="btn btn-warning btn-circle btn-sm" data-id="${articulo.id}">
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

    // Fetch proveedores
    function obtenerProveedores() {
        fetch("http://localhost:9090/api/v1/proveedores")
            .then(response => response.json())
            .then(data => {
                const proveedorSelect = document.getElementById("proveedor");
                // Limpiar cualquier opción previa
                proveedorSelect.innerHTML = '';
                // Agregar una opción por defecto
                const defaultOption = document.createElement("option");
                defaultOption.textContent = "Seleccione un proveedor";
                proveedorSelect.appendChild(defaultOption);
                // Agregar opciones para cada proveedor
                data.forEach(proveedor => {
                    const option = document.createElement("option");
                    option.value = proveedor.id; // Asignar el ID del proveedor como valor
                    option.textContent = proveedor.nombreProveedor;
                    proveedorSelect.appendChild(option);
                });
            })
            .catch(error => {
                console.error("Error al obtener los proveedores:", error);
            });
    }

// Llamar a la función para obtener proveedores cuando se abra el modal para crear un nuevo artículo
    document.addEventListener("DOMContentLoaded", function () {
        document.getElementById("crearArticuloModal").addEventListener("show.bs.modal", function (event) {
            obtenerProveedores();
        });
    });

});

$(document).ready(function() {
    $('#dataTable').DataTable();

    // Enviar el formulario de creación de artículo
    $('#guardarArticulo').click(function() {
        var formData = {
            nombreArticulo: $('#nombre').val(),
            cantidadArticulo: $('#cantidad').val(),
            proveedorPredeterminado: $('#proveedor').val()  // Captura el valor del proveedor seleccionado
        };

        $.ajax({
            type: 'POST',
            url: 'http://localhost:9090/api/v1/articulos',
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
                    <td>${response.proveedorPredeterminado ? response.proveedorPredeterminado.nombreProveedor : 'No asignado'}</td>
                    <td>
                        <div style="align-content: center">
                            <a href="#" class="btn btn-info btn-circle btn-sm" data-id="${response.id}">
                                <i class="fas fa-link"></i>
                            </a>
                            <a href="#" class="btn btn-warning btn-circle btn-sm" data-id="${response.id}">
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
            error: function(error) {
                // Manejar la respuesta de error
                alert('Error al crear el artículo');
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
