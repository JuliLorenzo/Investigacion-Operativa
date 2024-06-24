document.addEventListener("DOMContentLoaded", function() {
    // Fetch artículos para filtrar
    function cargarArticulos() {
        fetch("http://localhost:9090/api/v1/articulos")
            .then(response => response.json())
            .then(data => {
                const articuloSelect = document.querySelector("#filtroArticulo");
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

    // Fetch predicciones
    function cargarPredicciones() {
        fetch("http://localhost:9090/api/v1/prediccionesdemandas")
            .then(response => response.json())
            .then(data => {
                const tableBody = document.querySelector("#predicciones-table tbody");
                tableBody.innerHTML = ''; // Clear existing rows
                data.forEach(prediccion => {
                    const fechaPrediccion = new Date(prediccion.fechaPrediccion);
                    const mes = fechaPrediccion.getMonth() + 1;
                    const anio = fechaPrediccion.getFullYear();
                    const row = document.createElement("tr");
                    row.innerHTML = `
                        <td>${prediccion.id}</td>
                        <td>${mes}</td>
                        <td>${anio}</td>
                        <td>${prediccion.valorPrediccion}</td>
                        <td>${prediccion.articulo.nombreArticulo}</td>
                        <td>${prediccion.nombreMetodoUsado}</td>
                    `;
                    tableBody.appendChild(row);
                });
            })
            .catch(error => {
                console.error("Error al obtener las predicciones:", error);
            });
    }

    const articuloSelect = document.querySelector("#filtroArticulo");
    articuloSelect.addEventListener("focus", cargarArticulos);

    articuloSelect.addEventListener("change", function () {
        const selectedArticuloId = articuloSelect.value;
        if (selectedArticuloId) {
            fetch(`http://localhost:9090/api/v1/prediccionesdemandas/buscar/${selectedArticuloId}`)
                .then(response => response.json())
                .then(data => {
                    const tableBody = document.querySelector("#predicciones-table tbody");
                    tableBody.innerHTML = ''; // Clear existing rows
                    data.forEach(prediccion => {
                        const fechaPrediccion = new Date(prediccion.fechaPrediccion);
                        const mes = fechaPrediccion.getMonth() + 1;
                        const anio = fechaPrediccion.getFullYear();
                        const row = document.createElement("tr");
                        row.innerHTML = `
                            <td>${prediccion.id}</td>
                            <td>${mes}</td>
                            <td>${anio}</td>
                            <td>${prediccion.valorPrediccion}</td>
                            <td>${prediccion.articulo.nombreArticulo}</td>
                            <td>${prediccion.nombreMetodoUsado}</td>
                        `;
                        tableBody.appendChild(row);
                    });
                })
                .catch(error => {
                    console.error("Error al obtener las predicciones filtradas:", error);
                });
        } else {
            cargarPredicciones(); // Load all predictions if no article is selected
        }
    });

    // Initially load all predictions
    cargarPredicciones();
});
