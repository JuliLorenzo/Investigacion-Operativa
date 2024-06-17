document.addEventListener("DOMContentLoaded", function() {
    // Fetch artículos
    fetch("http://localhost:9090/api/v1/articulos/reponer")
        .then(response => response.json())
        .then(data => {
            const tableBody = document.querySelector("#articulos-faltantes-table tbody");
            data.forEach(articuloReponer => {
                // Filtrar artículos que no tienen una orden de compra activa
                if (!articuloReponer.ordenActiva) {
                    const row = document.createElement("tr");
                    row.innerHTML = `
                        <td>${articuloReponer.idArticulo}</td>
                        <td>${articuloReponer.nombreArticulo}</td>
                        <td>${articuloReponer.stockActualArticulo}</td>
                        <td>${articuloReponer.puntoPedido}</td>
                        <td>NO</td>
                    `;
                    tableBody.appendChild(row);
                }
            });
        })
        .catch(error => {
            console.error("Error al obtener los artículos faltantes", error);
        });
});
