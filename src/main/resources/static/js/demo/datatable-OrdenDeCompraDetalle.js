document.addEventListener("DOMContentLoaded", function() {
    const idOC = parseInt(localStorage.getItem('idOCSeleccionada'), 10);
    console.log("EL id de la OC es: ", idOC)

    fetch(`http://localhost:9090/api/v1/ordenescomprasdetalles/buscarDetallesPorOC/${idOC}`)
        .then(response => response.json())
        .then(data => {
            const tableBody = document.querySelector("#ordenesdecompradetalle-table tbody");

            data.forEach(ordenCompraDetalle => {
                const row = document.createElement("tr");
                row.innerHTML = `
                            <td>${ordenCompraDetalle.id}</td>
                            <td>${idOC}</td>
                            <td>${ordenCompraDetalle.articulo.nombreArticulo}</td>
                            <td>${ordenCompraDetalle.cantidadAComprar}</td>
                    `;
                    tableBody.appendChild(row);
            });
        })
        .catch(error => {
            console.error("Error al obtener las órdenes de compra:", error);
        });
});



