document.addEventListener("DOMContentLoaded", function() {
    // Fetch artículos
    fetch("http://localhost:9090/api/v1/articulos/faltantes")
        .then(response => response.json())
        .then(data => {
            const tableBody = document.querySelector("#articulos-faltantes-table tbody");
            data.forEach(articuloFaltante => {
                const row = document.createElement("tr");
                var ordenCompraActiva;
                if (articuloFaltante.ordenActiva){
                    ordenCompraActiva = "SI";
                } else {
                    ordenCompraActiva = "NO";
                }
                row.innerHTML = `
                    <td>${articuloFaltante.idArticulo}</td>
                    <td>${articuloFaltante.nombreArticulo}</td>
                    <td>${articuloFaltante.stockActualArticulo}</td>
                    <td>${articuloFaltante.stockSeguridad}</td>
                    <td>${ordenCompraActiva}</td>
                    
                `;
                tableBody.appendChild(row);
            });
        })
        .catch(error => {
            console.error("Error al obtener los artículos faltantes", error);
        })
        });
