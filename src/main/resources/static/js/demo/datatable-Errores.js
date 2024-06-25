document.addEventListener("DOMContentLoaded", function() {
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
    // CARGA LOS ARTICULOS CUANDO SE CARGA LA PAGINA
    cargarArticulos();

    // FILTRO DE ERRORES POR ARTICULO
    const filtroArticuloSelect = document.querySelector("#articulo");
    filtroArticuloSelect.addEventListener("change", function() {
        const selectedArticuloId = filtroArticuloSelect.value;
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


    //ESTO ES PARA QUE INGRESE LOS COEFICIENTES DE PONDERACION SEGUN LA CANTIDAD DE PERIODOS
    document.querySelector("#cantidadPeriodos").addEventListener("input", function() {
        const coefContainer = document.querySelector("#coefPondContainer");
        const coefAclaracion = document.querySelector("#coefPondAclaracion");
        coefContainer.innerHTML = ''; // Borra inputs previos
        const cantidadPeriodos = parseInt(this.value);
        if(cantidadPeriodos > 0){
            coefContainer.style.display = 'block';
            coefContainer.appendChild(coefAclaracion);
            for (let i = 0; i < cantidadPeriodos; i++) {
                const input = document.createElement("input");
                input.type = "number";
                input.className = "form-control";
                input.name = `coefPond${i + 1}`;
                input.placeholder = `Coeficiente ${i + 1}`;
                coefContainer.appendChild(input);
            }
        } else {
            coefContainer.style.display = 'none';
        }

    });

    //PARA CREAR EL ERROR, ES EL POST
    document.querySelector("#guardarError").addEventListener("click", function() {
        const form = document.querySelector("#crearErrorForm");
        const formData = {
            cantidadPeriodosHistoricos: document.querySelector('#cantidadPeriodos').value,
            cantidadPeriodosAdelante: 1,
            coeficientesPonderacion: [],
            idArticulo: document.querySelector('#idArticulo').value,
            mesAPredecir: null,
            anioAPredecir: null,
            alfa: document.querySelector('#alfa').value,
            nombreMetodoPrediccion: null,
            cantidadDemandaAnualTotal: document.querySelector('#demandaAnual').value,
            fechaDesde: document.querySelector('#fechaDesde').value,
            fechaHasta: document.querySelector('#fechaHasta').value
        };
        //agregar coef al formData
        for (let i = 0; i < formData.cantidadPeriodosHistoricos; i++) {
            const coefValue = parseFloat(document.querySelector(`input[name="coefPond${i + 1}"]`).value);
            formData.coeficientesPonderacion.push(coefValue);
        }

        console.log("Enviando datos... :", formData);

        fetch("http://localhost:9090/api/v1/errores/crearErrores", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(formData)
        })
            .then(response => {
                if(!response.ok){
                    throw new Error("Error en la respuesta del servidor" + response.statusText);
                }
                return response.json();
            })
            .then(data => {
                if (data) {
                    cargarErrores();
                    $('#crearErrorModal').modal('hide');
                } else {
                    console.error("Error al guardar el error:", data.message);
                }
            })
            .catch(error => {
                console.error("Error al guardar el error:", error);
            })
    });
});