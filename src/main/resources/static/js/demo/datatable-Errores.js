document.addEventListener("DOMContentLoaded", function() {
    // Fetch artículos
    fetch("http://localhost:9090/api/v1/errores")
        .then(response => response.json())
        .then(data => {
            const tableBody = document.querySelector("#errores-table tbody");
            data.forEach(errorMetodo => {
                const row = document.createElement("tr");
                const articulo = errorMetodo.articulo.nombreArticulo;
                row.innerHTML = `
                    <td>${errorMetodo.id}</td>
                    <td>${errorMetodo.porcentajeError}</td>
                    <td>${errorMetodo.fechaDesde}</td>
                    <td>${errorMetodo.fechaHasta}</td>
                    <td>${articulo}</td>
                    <td>${errorMetodo.nombreMetodoUsado}</td>
                    <td>${errorMetodo.valorDemandaReal}</td>
                    <td>${errorMetodo.valorPrediccionDemanda}</td>
                `;
                tableBody.appendChild(row);
            });
        })
        .catch(error => {
            console.error("Error al obtener los errores:", error);
        });

})
