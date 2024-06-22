document.addEventListener("DOMContentLoaded", function() {
    fetch("http://localhost:9090/api/v1/ordenescomprasdetalles")
        .then(response => response.json())
        .then(data => {
            const tableBody = document.querySelector("#ordenesdecompradetalle-table tbody");

            // Iterar sobre cada detalle de orden de compra
            data.forEach(ordenesdecomprasdetalles => {
                const row = document.createElement("tr");

                // Hacer una solicitud para obtener el nombre del artículo
                fetch(`http://localhost:9090/api/v1/articulos/${ordenesdecomprasdetalles.articulo.id}`)
                    .then(response => response.json())
                    .then(articulo => {
                        // Agregar los datos al row de la tabla
                        row.innerHTML = `
                            <td>${ordenesdecomprasdetalles.id}</td>
                            <td>${articulo.nombreArticulo}</td>
                            <td>${ordenesdecomprasdetalles.cantidadAComprar}</td>
                            <td>${ordenesdecomprasdetalles.ordenCompra ? ordenesdecomprasdetalles.ordenCompra.id : 'No asignado'}</td>
                            <td>
                                <div style="text-align: center">
                                    <a href="#" class="btn btn-info btn-circle btn-sm" data-id="${ordenesdecomprasdetalles.id}">
                                        <i class="fas fa-link"></i>
                                    </a>
                                    <a href="#" class="btn btn-warning btn-circle btn-sm" data-id="${ordenesdecomprasdetalles.id}">
                                        <i class="fas fa-edit"></i>
                                    </a>
                                </div>
                            </td>
                        `;
                        tableBody.appendChild(row);
                    })
                    .catch(error => {
                        console.error("Error al obtener el artículo:", error);
                    });
            });
        })
        .catch(error => {
            console.error("Error al obtener los detalles de las órdenes de compra:", error);
        });
});
