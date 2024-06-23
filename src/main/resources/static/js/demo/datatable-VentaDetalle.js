document.addEventListener("DOMContentLoaded", function() {
    fetch("http://localhost:9090/api/v1/ventas")
        .then(response => response.json())
        .then(data => {
            //console.log(data); // Verifica la estructura de los datos
            const tableBody = document.querySelector("#detalleventa-table tbody");
            data.forEach(venta => {
                venta.ventaDetalles.forEach(detalle => {
                    const row = document.createElement("tr");
                    row.innerHTML = `
                    <td>${detalle.id}</td>
                    <td>${venta.id}</td>
                    <td>${venta.fechaVenta}</td>
                    <td>${detalle.articulo.nombreArticulo}</td>
                    <td>${detalle.cantidadVendida}</td>
                `;
                    tableBody.appendChild(row);
                });
            });
        })
        .catch(error => {
            console.error("Error al obtener los detalles de venta:", error);
        });
});