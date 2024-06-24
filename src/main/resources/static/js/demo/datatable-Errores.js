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

    //PARA CREAR ERROR
    function cargarModelos() {
        const modelos = ["PROMEDIO_MOVIL_PONDERADO", "PROMEDIO_MOVIL_SUAVIZADO", "REGRESION_LINEAL", "ESTACIONALIDAD"]; // Reemplaza esto con una llamada fetch si necesitas cargar los modelos desde el backend.
        const modeloSelect = document.querySelector("#modeloPrediccion");
        modelos.forEach(modelo => {
            const option = document.createElement("option");
            option.textContent = modelo;
            option.value = modelo;
            modeloSelect.appendChild(option);
        });
    }
    cargarModelos();



    //crear dinamicamente espacios para coef de ponderacion
    document.querySelector("#cantidadPeriodos").addEventListener("input", function() {
        const coefContainer = document.querySelector("#coefPondContainer");
        coefContainer.innerHTML = ''; // Clear previous inputs
        const cantidadPeriodos = this.value;
        for (let i = 0; i < cantidadPeriodos; i++) {
            const input = document.createElement("input");
            input.type = "number";
            input.className = "form-control";
            input.name = `coefPond${i + 1}`;
            input.placeholder = `Coeficiente ${i + 1}`;
            coefContainer.appendChild(input);
        }
    });

    //envia datos crear error
    document.querySelector("#guardarError").addEventListener("click", function() {
        const form = document.querySelector("#crearErrorForm");
        const formData = new FormData(form);
        const jsonData = {};
        formData.forEach((value, key) => {
            if (key.startsWith("coefPond")) {
                if (!jsonData.coefPonderacion) {
                    jsonData.coefPonderacion = [];
                }
                jsonData.coefPonderacion.push(parseFloat(value));
            } else {
                jsonData[key] = value;
            }
        });

        fetch("http://localhost:9090/api/v1/errores/calcularerror", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(jsonData)
        })
            .then(response => response.json())
            .then(data => {
                if (data) {
                    cargarErrores();
                    $('#crearErrorModal').modal('hide');
                } else {
                    console.error("Error al guardar el error:", data.message);
                }
            })
    });
});
