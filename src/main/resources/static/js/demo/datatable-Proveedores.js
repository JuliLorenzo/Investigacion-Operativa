// Call the dataTables jQuery plugin
$(document).ready(function() {
    $('#dataTable').DataTable();
});



document.addEventListener("DOMContentLoaded", function() {
    fetch("http://localhost:9090/api/v1/proveedores")
        .then(response => response.json())
        .then(data => {
            const tableBody = document.querySelector("#proveedores-table tbody");
            data.forEach(proveedor => {
                const row = document.createElement("tr");
                row.innerHTML = `
                    <td>${proveedor.id}</td>
                    <td>${proveedor.nombreProveedor}</td>
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
            console.error("Error al obtener los proveedores:", error);
        });
});