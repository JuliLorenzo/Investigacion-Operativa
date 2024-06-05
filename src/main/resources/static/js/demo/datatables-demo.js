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
                    <td>${articulo.loteOptimoArticulo}</td>
                    <td>${articulo.puntoPedidoArticulo}</td>
                    <td>${articulo.stockSeguridadArticulo}</td>
                    <td>${articulo.cgiArticulo}</td>
                `;
          tableBody.appendChild(row);
        });
      })
      .catch(error => {
        console.error("Error al obtener los artículos:", error);
      });
});