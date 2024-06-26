document.addEventListener("DOMContentLoaded", function() {
    fetch("http://localhost:9090/api/v1/articulos/reponer")
        .then(response => response.json())
        .then(data => {
            console.log(data); // Añadir esta línea para verificar la respuesta en la consola
            const tableBody = document.getElementById("tableBody");
            data.forEach(articuloReponer => {
                const row = document.createElement("tr");
                row.innerHTML = `
                    <td>${articuloReponer.idArticulo}</td>
                    <td>${articuloReponer.nombreArticulo}</td>
                    <td>${articuloReponer.cantidadArticulo}</td>
                    <td>${articuloReponer.puntoPedido}</td>
                `;
                tableBody.appendChild(row);
            });
            $('#articulos-areponer-table').DataTable();
        })
        .catch(error => {
            console.error("Error al obtener los artículos a reponer:", error);
        });
});

$(document).ready(function() {
    $('#articulos-areponer-table').DataTable({
        "paging": false, // Deshabilita la paginación
        searching: false, // Desactiva la función de búsqueda
        info: false,      // Desactiva la información de 'Showing X of X entries'
    });
});