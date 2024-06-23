document.addEventListener("DOMContentLoaded", function() {
    fetch("http://localhost:9090/api/v1/demandashistoricas")
        .then(response => response.json())
        .then(data => {
            //console.log(data); // Verifica la estructura de los datos <td>${demandashistoricas.ventaList ? demandashistoricas.ventaList.map(venta => venta.detalle).join(', ') : 'N/A'}</td>
            const tableBody = document.querySelector("#demandahistorica-table tbody");
            data.forEach(demandashistoricas => {
                const row = document.createElement("tr");
                row.innerHTML = `
                <td>${demandashistoricas.id}</td>
                <td>${demandashistoricas.fechaDesde}</td>
                <td>${demandashistoricas.fechaHasta}</td>
                <td>${demandashistoricas.cantidadVendida > 0 ? demandashistoricas.cantidadVendida : 0}</td>
                <td>${demandashistoricas.articulo ? demandashistoricas.articulo.nombreArticulo : 'N/A'}</td>             
                <td>
                    <div style="text-align: center">
                        <a href="#" class="btn btn-info btn-circle btn-sm" data-id="${demandashistoricas.id}">
                            <i class="fas fa-link"></i>
                        </a>
                        <a href="#" class="btn btn-warning btn-circle btn-sm" data-id="${demandashistoricas.id}">
                            <i class="fas fa-edit"></i>
                        </a>
                    </div>
                </td>
            `;
                tableBody.appendChild(row);
            });
        })
        .catch(error => {
            console.error("Error al obtener los proveedores:", error);
        });

    //Trae los articulos al crear
    function cargarArticulos() {
        fetch("http://localhost:9090/api/v1/articulos")
            .then(response => response.json())
            .then(data => {
                const articuloSelect = document.querySelector("#articulo");
                articuloSelect.innerHTML = '<option value="">Seleccione un articulo</option>';
                data.forEach(articulo => {
                    const option = document.createElement("option");
                    option.textContent = articulo.nombreArticulo;
                    option.value = articulo.id;
                    articuloSelect.appendChild(option);
                });
            })
            .catch(error => {
                console.error('Error al obtener la lista de articulos:', error);
            });
    }

// Mostrar el modal y cargar proveedores al hacer clic en el botón para crear un artículo
    $('#crearDemandaHistoricaModal').on('show.bs.modal', function () {
        cargarArticulos();
    });

    $(document).ready(function() {
        $('#demandahistorica-table').DataTable();
        $('#guardarDemandaHistorica').click(function(event) {
            event.preventDefault();
            var articuloId = parseInt($('#articulo').val());
            var articuloNombre = $('#articulo option:selected').text();

            var formData = {

                fechaDesde: $('#fechaDesde').val(),
                fechaHasta: $('#fechaHasta').val(),
                idArticulo: articuloId

            };
            console.log('Enviando datos:', JSON.stringify(formData)); // Depuración

            $.ajax({
                type: 'POST',
                url: 'http://localhost:9090/api/v1/demandashistoricas/calcularDemandaHistorica',
                contentType: 'application/json',
                data: JSON.stringify(formData),
                success: function(response) {
                    console.log("Demanda Historica creada correctamente: ", response); //para encontrar error
                    $('#crearDemandaHistoricaModal').modal('hide');
                    alert('Demanda Historica creada exitosamente');

                    // Agregar el nuevo artículo a la tabla
                    const tableBody = $('#demandahistorica-table tbody');
                    const row = document.createElement("tr");
                    row.innerHTML = `
                    <td>${response.id}</td>
                    <td>${response.fechaDesde}</td>
                    <td>${response.fechaHasta}</td>
                    <td>${response.cantidadVendida > 0 ? response.cantidadVendida : 0}</td>
                    <td>${response.articulo ? response.articulo.nombreArticulo : 'N/A'}</td>             
                    <td>
                    <div style="text-align: center">
                        <a href="#" class="btn btn-info btn-circle btn-sm" data-id="${response.id}">
                            <i class="fas fa-link"></i>
                        </a>
                        <a href="#" class="btn btn-warning btn-circle btn-sm" data-id="${response.id}">
                            <i class="fas fa-edit"></i>
                        </a>
                    </div>
                </td>
                `;
                    tableBody.append(row);

                    // Limpia el formulario después de enviar
                    $('#crearDemandaHistoricaForm')[0].reset();
                },
                error: function(xhr, status, error) {
                    console.error('Error al crear la demanda historica:', xhr.responseText);
                    alert('Error al crear la demanda historica: ' + (xhr.responseJSON && xhr.responseJSON.message ? xhr.responseJSON.message : 'Desconocido'));
                }
            });
        });
    });

});

