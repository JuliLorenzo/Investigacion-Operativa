document.addEventListener("DOMContentLoaded", function() {
    // Fetch artículos
    function cargarArticulos() {
        fetch("http://localhost:9090/api/v1/articulos")
            .then(response => response.json())
            .then(data => {
                const articuloSelect = document.querySelector("#articulo");
                articuloSelect.innerHTML = '<option value="">Seleccione un artículo</option>';
                data.forEach(articulo => {
                    const option = document.createElement("option");
                    option.textContent = articulo.nombreArticulo;
                    option.value = articulo.id;
                    articuloSelect.appendChild(option);
                });
            })
            .catch(error => {
                console.error('Error al obtener la lista de artículos:', error);
            });
    }

    // Fetch errores
    function cargarErrores() {
        fetch("http://localhost:9090/api/v1/errores")
            .then(response => response.json())
            .then(data => {
                const tableBody = document.querySelector("#errores-table tbody");
                tableBody.innerHTML = ''; // Clear existing rows
                data.forEach(errorMetodo => {
                    const row = document.createElement("tr");
                    const articulo = errorMetodo.articulo.nombreArticulo;
                    const porcentajeErrorTruncado = Math.trunc(errorMetodo.porcentajeError * 1000) / 1000;
                    const errorTotalTruncado = Math.trunc(errorMetodo.errorTotal * 1000) / 1000;

                    row.innerHTML = `
                        <td>${errorMetodo.id}</td>
                        <td>${porcentajeErrorTruncado}</td>
                        <td>${errorTotalTruncado}</td>
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
    }

    // Load articles on dropdown focus
    const articuloSelect = document.querySelector("#articulo");
    articuloSelect.addEventListener("focus", cargarArticulos);

    // Filter errors based on selected article
    articuloSelect.addEventListener("change", function() {
        const selectedArticuloId = articuloSelect.value;
        if (selectedArticuloId) {
            fetch(`http://localhost:9090/api/v1/errores/buscar/${selectedArticuloId}`)
                .then(response => response.json())
                .then(data => {
                    const tableBody = document.querySelector("#errores-table tbody");
                    tableBody.innerHTML = ''; // Clear existing rows
                    data.forEach(errorMetodo => {
                        const row = document.createElement("tr");
                        const articulo = errorMetodo.articulo.nombreArticulo;
                        const porcentajeErrorTruncado = Math.trunc(errorMetodo.porcentajeError * 1000) / 1000;
                        const errorTotalTruncado = Math.trunc(errorMetodo.errorTotal * 1000) / 1000;
                        row.innerHTML = `
                            <td>${errorMetodo.id}</td>
                            <td>${porcentajeErrorTruncado}</td>
                            <td>${errorTotalTruncado}</td>
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
                    console.error("Error al obtener los errores filtrados:", error);
                });
        } else {
            cargarErrores(); // Load all errors if no article is selected
        }
    });

    // Initial load of errors
    cargarErrores();
});
