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
            data.forEach(venta => {
                const row = document.createElement("tr");
                row.innerHTML = `
                    <td>${venta.id}</td>
                    <td>${venta.fechaVenta}</td>
                `;
                tableBody.appendChild(row);
            });
        })
        .catch(error => {
            console.error("Error al obtener las ventas:", error);
        });

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
                defaultOption.disabled = true;
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
});
