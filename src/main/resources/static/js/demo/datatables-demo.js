// Call the dataTables jQuery plugin
$(document).ready(function() {
  $('#dataTable').DataTable();
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
                                <i class="fas fa-pencil"></i>
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