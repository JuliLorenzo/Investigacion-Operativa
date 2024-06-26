// Call the dataTables jQuery plugin
$(document).ready(function() {
    $('#dataTable').DataTable();
});

document.addEventListener("DOMContentLoaded", function() {
    // Fetch and display ventas in the table
    fetch("http://localhost:9090/api/v1/ventas")
        .then(response => response.json())
        .then(data => {
            const tableBody = document.querySelector("#ventas-table tbody");
            let i = 0;
            data.forEach(venta => {
                const row = document.createElement("tr");
                const idVenta = venta.id;

                row.innerHTML = `
                    <td>${venta.id}</td>
                    <td>${venta.fechaVenta}</td>
                    <td>
                        <a href="VentaDetalle.html" class="btn btn-primary btn-icon-split ver-detalles"  data-id-venta="${venta.id}">
                            <span class="text">Ver detalles</span>
                        </a>
                    </td>
                `;
                tableBody.appendChild(row);
                const verDetallesBtn = row.querySelector('.ver-detalles');
                verDetallesBtn.addEventListener('click', function(event){
                    event.preventDefault();
                    const idVentaAMandar = this.getAttribute('data-id-venta');
                    localStorage.setItem('idVentaSeleccionada', idVenta);
                    window.location.href = "VentaDetalle.html"
                });


            });
        })
        .catch(error => {
            console.error("Error al obtener las ventas:", error);
        });



    /*
    * document.querySelectorAll('.ver-detalles').forEach(button => {
        button.addEventListener('click', function() {
            const idVenta = this.getAttribute('data-id-venta');
            if (idVenta) {
                sessionStorage.setItem('idVenta', idVenta);
                console.log(`ID de venta ${idVenta} guardado en sessionStorage.`);
                window.location.href = 'VentaDetalle.html';
            } else {
    let j = 0;
    document.querySelectorAll('.ver-detalles').forEach(button => {
        button.add()('click', function(){
            const idVenta = this.getAttribute('data-id-venta');
            if(idVenta){
                sessionStorage.setItem('idVenta', idVenta);
                localStorage.setItem('idVenta', idVenta);
                //console.log("EL id anets de mandar es: ",localStorage.getItem(idVenta));

                window.location.href = `VentaDetalle.html`;
            }else{
                console.error('ID de venta no encontrado.');
            }
        });
        j++;
    });
    document.getElementById('ver-detalles').addEventListener('click', function() {
        const idVenta = this.getAttribute('data-id-venta');
        if(idVenta){
            sessionStorage.setItem('idVenta', idVenta);
            localStorage.setItem('idVenta', idVenta);
            //console.log("EL id anets de mandar es: ",localStorage.getItem(idVenta));

            window.location.href = `VentaDetalle.html`;
        }else{
            console.error('ID de venta no encontrado.');
        }
    });*/

    // Agregar funcionalidad para agregar detalles dinámicos
    document.getElementById('agregarDetalleBtn').addEventListener('click', function() {
        agregarDetalle();
    });

    function agregarDetalle() {
        const detallesContainer = document.getElementById('detallesContainer');
        const articuloIndex = detallesContainer.children.length;

        const detalleDiv = document.createElement('div');
        detalleDiv.className = 'form-group detalle-item';

        // Fetch artículos data
        fetch("http://localhost:9090/api/v1/articulos")
            .then(response => response.json())
            .then(articulos => {
                // Create select element
                const select = document.createElement('select');
                select.className = 'form-control';
                select.id = `articulo-${articuloIndex}`;
                select.name = `articulos[]`;
                select.required = true;

                // Add default option
                const defaultOption = document.createElement('option');
                defaultOption.value = '';
                defaultOption.textContent = 'Seleccione un artículo';
                // defaultOption.disabled = true;
                defaultOption.selected = true;
                select.appendChild(defaultOption);

                // Populate select with artículos
                articulos.forEach(articulo => {
                    const option = document.createElement('option');
                    option.value = articulo.id;  // Store ID in value
                    option.textContent = articulo.nombreArticulo;  // Display name
                    select.appendChild(option);
                });

                // Create input element for quantity
                const cantidadInput = document.createElement('input');
                cantidadInput.type = 'number';
                cantidadInput.className = 'form-control';
                cantidadInput.id = `cantidad-${articuloIndex}`;
                cantidadInput.name = `cantidades[]`;
                cantidadInput.placeholder = 'Cantidad';
                cantidadInput.required = true;
                cantidadInput.min = '1';

                // Append elements to detalleDiv
                detalleDiv.innerHTML = `
                    <div style="align-items: center">
                        <label for="articulo-${articuloIndex}">Articulo ${articuloIndex + 1}</label>
                    </div>
                `;
                detalleDiv.appendChild(select);
                detalleDiv.appendChild(cantidadInput);
                detalleDiv.innerHTML += `
                    <button type="button" class="btn btn-danger removeDetalleBtn">
                        <i class="fas fa-trash"></i>
                    </button>
                `;
                detallesContainer.appendChild(detalleDiv);

                // Agregar evento para eliminar el detalle
                detalleDiv.querySelector('.removeDetalleBtn').addEventListener('click', function() {
                    detalleDiv.remove();
                });
            })
            .catch(error => {
                console.error("Error al obtener los artículos:", error);
            });
    }

    document.getElementById('guardarVenta').addEventListener('click', function(event) {
        event.preventDefault();

        const detallesContainer = document.getElementById('detallesContainer');
        const detalles = detallesContainer.querySelectorAll('.detalle-item');

        // Obtener la fecha de venta del input de tipo "date"
        const fechaVentaInput = document.getElementById('fecha').value;

        // Convertir la fecha a un objeto Date para incluir la hora
        const fechaHora = new Date(fechaVentaInput);
        fechaHora.setHours(new Date().getHours());
        fechaHora.setMinutes(new Date().getMinutes());
        fechaHora.setSeconds(new Date().getSeconds());

        const articulosDetalleVenta = {};
        detalles.forEach((detalle, index) => {
            const articuloId = detalle.querySelector(`select`).value;
            const cantidad = detalle.querySelector(`input[type="number"]`).value;
            articulosDetalleVenta[articuloId] = parseInt(cantidad, 10);
        });

        const ventaDto = {
            fechaHora: fechaHora.toISOString(),
            articulosDetalleVenta: articulosDetalleVenta
        };

        fetch("http://localhost:9090/api/v1/ventas/crearVenta", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(ventaDto)
        })
            .then(response => {
                if (response.ok) {
                    alert("Venta creada exitosamente");
                    location.reload(); // Recargar la página
                } else {
                    const errorMessage = response.headers.get("Error-Message");
                    throw new Error(errorMessage || "Error desconocido");
                }
            })
            .catch(error => {
                console.error("Error al enviar la venta:", error);
                alert(`Error al crear la venta: ${error.message}`);
            });
    });
});