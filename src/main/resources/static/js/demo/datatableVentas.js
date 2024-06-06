// Call the dataTables jQuery plugin
$(document).ready(function() {
  $('#dataTable').DataTable();
});
document.addEventListener("DOMContentLoaded", function() {
    fetch("http://localhost:9090/api/v1/ventas")
        .then(response => response.json())
        .then(data => {
            const tableBody = document.querySelector("#ventas-table tbody");
            data.forEach(venta => {
                const row = document.createElement("tr");
                row.innerHTML = `
                    <td>${venta.id}</td>
                    <td>${venta.fechaAlta}</td>
                    <td>${venta.fechaBaja}</td>
                    <td>${venta.fechaVenta}</td>
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
            console.error("Error al obtener las ventas:", error);
        });
});