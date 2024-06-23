document.addEventListener("DOMContentLoaded", function() {
    fetch("http://localhost:9090/api/v1/ordenescomprasdetalles")
        .then(response => response.json())
        .then(data => {
            const tableBody = document.querySelector("#ordenesdecompradetalle-table tbody");
            data.forEach(ordencompra => {
                ordencompra.ordenCompraDetalles.forEach(detalle => {
                    const row = document.createElement("tr");
                    row.innerHTML = `
                    <td>${detalle.id}</td>
                    <td>${ordencompra.id}</td>
                    <td>${ordencompra.fechaOrdenCompra}</td>
                    <td>${detalle.articulo.nombreArticulo}</td>
                    <td>${detalle.cantidadAComprar}</td>
                    `;
                    tableBody.appendChild(row);
                });
            })
        .catch(error => {
            console.error("Error al obtener los detalles de las órdenes de compra:", error);
        });
});
})
