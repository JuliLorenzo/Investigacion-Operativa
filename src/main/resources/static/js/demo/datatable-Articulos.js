// Call the dataTables jQuery plugin
$(document).ready(function() {
  $('#dataTable').DataTable();
});

// Enviar el formulario de creación de artículo
$('#guardarArticulo').click(function() {
    var formData = {
        nombreArticulo: $('#nombre').val(),
        cantidadArticulo: $('#cantidad').val()
    };

    $.ajax({
        type: 'POST',
        url: 'http://localhost:9090/api/v1/articulos',  // Asegúrate de que la URL coincida con el endpoint en tu controlador
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
                  <td>
                      <div style="align-content: center">
                          <a href="#" class="btn btn-info btn-circle btn-sm">
                              <i class="fas fa-link"></i>
                          </a>
                          <a href="#" class="btn btn-warning btn-circle btn-sm">
                              <i class="fas fa-edit"></i>
                          </a>
                          <a href="#" class="btn btn-danger btn-circle btn-sm">
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

document.addEventListener("DOMContentLoaded", function() {
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
                    <td>
                        <div style="align-content: center">
                            <a href="#" class="btn btn-info btn-circle btn-sm">
                                <i class="fas fa-link"></i>
                            </a>
                            <a href="#" class="btn btn-warning btn-circle btn-sm">
                                <i class="fas fa-edit"></i>
                            </a>
                            <a href="#" class="btn btn-danger btn-circle btn-sm">
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
});
