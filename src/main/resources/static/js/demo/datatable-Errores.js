document.addEventListener("DOMContentLoaded", function() {
    // ARTICULOS PARA EL FILTRO Y PARA LA CREACION
    // ARTICULOS PARA EL FILTRO Y PARA LA CREACION
    function cargarArticulos() {
        fetch("http://localhost:9090/api/v1/articulos")
            .then(response => response.json())
            .then(data => {
                const articuloSelect = document.querySelector("#idArticulo");
                const filtroArticuloSelect = document.querySelector("#articulo");
                articuloSelect.innerHTML = '<option value="">Seleccione un artículo</option>';
                filtroArticuloSelect.innerHTML = '<option value="">Seleccione un artículo para filtrar</option>';
                data.forEach(articulo => {
                    const option1 = document.createElement("option");
                    option1.textContent = articulo.nombreArticulo;
                    option1.value = articulo.id;
                    articuloSelect.appendChild(option1);

                    const option2 = document.createElement("option");
                    option2.textContent = articulo.nombreArticulo;
                    option2.value = articulo.id;
                    filtroArticuloSelect.appendChild(option2);
                });
            })
            .catch(error => {
                console.error('Error al obtener la lista de artículos:', error);
            });
    }


    // TRAE TODOS LOS ERRORES A LA TABLA
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

    // CARGA LOS ARTICULOS UNA VEZ QUE SE CLICKEE EL LABEL DE CREAR
    const articuloSelect = document.querySelector("#idArticulo");
    articuloSelect.addEventListener("focus", cargarArticulos);

    // FILTRO DE ERRORES POR ARTICULO
    articuloSelect.addEventListener("change", function() {
        const selectedArticuloId = articuloSelect.value;
        if (selectedArticuloId) {
            //FETCH PARA BUSCAR LOS ERRORES POR ARTICULO
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

    // CARGA INICIAL DE ERRORES CUANDO LLEGA A LA PAGINA
    cargarErrores();

    //PARA CREAR ERROR
    //PARA CREAR ERROR
    //PARA CREAR ERROR
    //PARA CREAR ERROR

    //CARGA LOS MODELOS EN EL LABEL DE CREAR
    function cargarModelos() {
        const modelos = ["PROMEDIO_MOVIL_PONDERADO", "PROMEDIO_MOVIL_SUAVIZADO", "REGRESION_LINEAL", "ESTACIONALIDAD"]; // Reemplaza esto con una llamada fetch si necesitas cargar los modelos desde el backend.
        const modeloSelect = document.querySelector("#modeloPrediccion");
        modelos.forEach(modelo => {
            const option = document.createElement("option");
            option.textContent = modelo;
            option.value = modelo;
            modeloSelect.appendChild(option);
        });
        modeloSelect.addEventListener("change", manejarCambioModelo);
    }

    //ATRAPA LA SELECCION DE MODELO Y ELIGE QUE MOSTRAR
    function manejarCambioModelo(event){
        const selectedModel = event.target.value;
        const coefContainer = document.querySelector('#coefPondContainer');
        const prediccionPMPS = document.querySelector('#prediccionPMPS');
        const prediccionEst = document.querySelector('#prediccionEst');
        const cantidadPeriodos = document.querySelector('#cantidadPeriodosContainer');

        coefContainer.innerHTML = ''; //limpiar inputs previos
        prediccionPMPS.style.display = 'none';
        prediccionEst.style.display = 'none';
        cantidadPeriodos.style.display = 'none';
        coefContainer.style.display = 'none';

        switch(selectedModel){
            case "PROMEDIO_MOVIL_PONDERADO":
                cantidadPeriodos.style.display = 'block';
                coefContainer.style.display = 'block';
                break;
            case "PROMEDIO_MOVIL_SUAVIZADO":
                prediccionPMPS.style.display = 'block';
                break;
            case "REGRESION_LINEAL":
                cantidadPeriodos.style.display = 'block';
                break;
            case "ESTACIONALIDAD":
                prediccionEst.style.display = 'block';
                break;
        }
    }
    cargarModelos();

    //ESTO ES PARA QUE INGRESE LOS COEFICIENTES DE PONDERACION SEGUN LA CANTIDAD DE PERIODOS
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

    //PARA CREAR EL ERROR, ES EL POST
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