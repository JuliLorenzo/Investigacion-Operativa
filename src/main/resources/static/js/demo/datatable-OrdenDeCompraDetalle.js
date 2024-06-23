document.addEventListener("DOMContentLoaded", function() {
    fetch("http://localhost:9090/api/v1/ordenescompras")
        .then(response => response.json())
        .then(data => {
            const tableBody = document.querySelector("#ordenesdecompradetalle-table tbody");

            data.forEach(ordenCompra => {
                ordenCompra.ordenCompraDetalles.forEach(detalle => {
                    const row = document.createElement("tr");

                    fetch(`http://localhost:9090/api/v1/articulos/${detalle.articulo.id}`)
                        .then(response => response.json())
                        .then(articulo => {
                            row.innerHTML = `
                                <td>${detalle.id}</td>
                                <td>${ordenCompra.id}</td>
                                <td>${new Date(ordenCompra.fechaOrdenCompra).toLocaleDateString()}</td>
                                <td>${articulo.nombreArticulo}</td>
                                <td>${detalle.cantidadAComprar}</td>
                            `;
                            tableBody.appendChild(row);
                        })
                        .catch(error => {
                            console.error("Error al obtener el artículo:", error);
                        });
                });
            });
        })
        .catch(error => {
            console.error("Error al obtener las órdenes de compra:", error);
        });
});



