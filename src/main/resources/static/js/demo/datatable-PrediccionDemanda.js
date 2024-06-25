document.addEventListener("DOMContentLoaded", function () {
    $('#crearPrediccionModal').on('show.bs.modal', function () {
        cargarArticulos();
        cargarMeses();
        cargarAnios();
    });

    const metodoMapping = {
        'ESTACIONALIDAD': 'Estacionalidad',
        'PROMEDIO_MOVIL_PONDERADO': 'Promedio Móvil Ponderado',
        'PROMEDIO_MOVIL_SUAVIZADO': 'Promedio Móvil Suavizado',
        'REGRESION_LINEAL': 'Regresión Lineal'
    };
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
                cargarMetodo(articuloSelect);
            })
            .catch(error => {
                console.error('Error al obtener la lista de artículos:', error);
            });
    }
    function cargarMetodo(articuloSelect) {
        articuloSelect.addEventListener("change", function () {
            const selectedArticuloId = articuloSelect.value;
            if (selectedArticuloId) {
                fetch(`http://localhost:9090/api/v1/articulos/${selectedArticuloId}`)
                    .then(response => response.json())
                    .then(data => {
                        const metodoInput = document.querySelector("#metodo");
                        metodoInput.value = metodoMapping[data.metodoPrediccionPredeterminado] || 'Método Desconocido';
                        mostrarCamposExtra(data.metodoPrediccionPredeterminado);
                    })
                    .catch(error => {
                        console.error('Error al obtener el método de predicción predeterminado del artículo:', error);
                    });
            }
        })
    }
    function cargarMeses() {
        const meses = [
            "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        ];
        const mesSelect = document.querySelector("#mesAPredecir");
        meses.forEach((mes, index) => {
            const option = document.createElement("option");
            option.textContent = mes;
            option.value = index + 1;
            mesSelect.appendChild(option);
        });
    }

    function cargarAnios() {
        const anios = [2021, 2022, 2023, 2024];
        const anioSelect = document.querySelector("#anioAPredecir");
        anios.forEach(anio => {
            const option = document.createElement("option");
            option.textContent = anio;
            option.value = anio;
            anioSelect.appendChild(option);
        });
    }

    function mostrarCamposExtra(metodo) {
        const extraFields = document.querySelector("#camposExtra");
        extraFields.innerHTML = '';
        if (metodo === 'ESTACIONALIDAD') {
            extraFields.innerHTML = `
                <div class="form-group">
                    <label for="cantidadDemandaAnualTotal">Cantidad de Demanda Anual Total</label>
                    <input type="number" class="form-control" id="cantidadDemandaAnualTotal" name="cantidadDemandaAnualTotal">
                </div>
            `;
        } else if (metodo === 'PROMEDIO_MOVIL_PONDERADO') {
            extraFields.innerHTML = `
                <div class="form-group">
                    <label for="cantidadPeriodosHistoricos">Cantidad de Periodos Históricos</label>
                    <select class="form-control" id="cantidadPeriodosHistoricos" name="cantidadPeriodosHistoricos" onchange="actualizarCoeficientesPonderacion()">
                        ${[...Array(10)].map((_, i) => `<option value="${i + 3}">${i + 3}</option>`).join('')}
                    </select>
                </div>
                <div id="coeficientesPonderacion"></div>
            `;
        } else if (metodo === 'REGRESION_LINEAL') {
            extraFields.innerHTML = `
                <div class="form-group">
                    <label for="cantidadPeriodosHistoricos">Cantidad de Periodos Históricos</label>
                    <select class="form-control" id="cantidadPeriodosHistoricos" name="cantidadPeriodosHistoricos">
                        ${[...Array(10)].map((_, i) => `<option value="${i + 3}">${i + 3}</option>`).join('')}
                    </select>
                </div>
            `;
        } else if (metodo === 'PROMEDIO_MOVIL_SUAVIZADO') {
            extraFields.innerHTML = `
                <div class="form-group">
                    <label for="alfa">Alfa</label>
                    <input type="number" step="0.01" class="form-control" id="alfa" name="alfa">
                </div>
            `;
        }
    }

    window.actualizarCoeficientesPonderacion = function () {
        const cantidadPeriodosHistoricos = document.querySelector("#cantidadPeriodosHistoricos").value;
        const coeficientesPonderacion = document.querySelector("#coeficientesPonderacion");
        coeficientesPonderacion.innerHTML = '';
        for (let i = 0; i < cantidadPeriodosHistoricos; i++) {
            coeficientesPonderacion.innerHTML += `
                <div class="form-group">
                    <label for="coeficientePonderacion${i + 1}">Coeficiente de Ponderación ${i + 1}</label>
                    <input type="number" step="0.01" class="form-control" id="coeficientePonderacion${i + 1}" name="coeficientesPonderacion">
                </div>
            `;
        }
    }

    window.submitForm = function () {
        const form = document.querySelector("#crearPrediccionForm");
        const formData = new FormData(form);
        const datosPrediccion = {
            idArticulo: formData.get("articulo"),
            cantidadPeriodosAdelante: formData.get("cantidadPeriodosAdelante"),
            mesAPredecir: formData.get("mesAPredecir"),
            anioAPredecir: formData.get("anioAPredecir"),
            nombreMetodoPrediccion: formData.get("metodo"),
            cantidadDemandaAnualTotal: formData.get("cantidadDemandaAnualTotal") || null,
            cantidadPeriodosHistoricos: formData.get("cantidadPeriodosHistoricos") || null,
            coeficientesPonderacion: Array.from(document.querySelectorAll('[name="coeficientesPonderacion"]')).map(input => parseFloat(input.value)) || null,
            alfa: formData.get("alfa") || null
        };

        // Add other fields if necessary
        console.log(datosPrediccion);
        // You can now send datosPrediccion to your backend
    }

    // Fetch artículos para filtrar
    function cargarArticulosFiltro() {
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
                    const fechaPred = new Date(prediccion.fechaPrediccion + "T00:00:00");
                    const mes = (fechaPred.getMonth()) + 1;
                    const anio = fechaPred.getFullYear();
                    const metodoUsado = metodoMapping[prediccion.nombreMetodoUsado] || 'Método Desconocido';
                    const row = document.createElement("tr");
                    row.innerHTML = `
                        <td>${prediccion.id}</td>
                        <td>${mes}</td>
                        <td>${anio}</td>
                        <td>${prediccion.valorPrediccion}</td>
                        <td>${prediccion.articulo.nombreArticulo}</td>
                        <td>${metodoUsado}</td>
                    `;
                    tableBody.appendChild(row);
                });
            })
            .catch(error => {
                console.error("Error al obtener las predicciones:", error);
            });
    }

    const articuloSelect = document.querySelector("#filtroArticulo");
    articuloSelect.addEventListener("focus", cargarArticulosFiltro);

    articuloSelect.addEventListener("change", function () {
        const selectedArticuloId = articuloSelect.value;
        if (selectedArticuloId) {
            fetch(`http://localhost:9090/api/v1/prediccionesdemandas/buscar/${selectedArticuloId}`)
                .then(response => response.json())
                .then(data => {
                    const tableBody = document.querySelector("#predicciones-table tbody");
                    tableBody.innerHTML = ''; // Clear existing rows
                    data.forEach(prediccion => {
                        const fechaPred = new Date(prediccion.fechaPrediccion + "T00:00:00");
                        const mes = (fechaPred.getMonth()) + 1;
                        const anio = fechaPred.getFullYear();
                        const metodoUsado = metodoMapping[prediccion.nombreMetodoUsado] || 'Método Desconocido';
                        const row = document.createElement("tr");
                        row.innerHTML = `
                        <td>${prediccion.id}</td>
                        <td>${mes}</td>
                        <td>${anio}</td>
                        <td>${prediccion.valorPrediccion}</td>
                        <td>${prediccion.articulo.nombreArticulo}</td>
                        <td>${metodoUsado}</td>
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
})