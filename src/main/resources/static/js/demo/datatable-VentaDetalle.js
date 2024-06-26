document.addEventListener("DOMContentLoaded", function() {
    //const urlParams = new URLSearchParams(window.location.search);
    const idVenta = parseInt(localStorage.getItem('idVentaSeleccionada'), 10);

    console.log("El id de la venta es: ", typeof idVenta);
        fetch(`http://localhost:9090/api/v1/ventasdetalles/buscarDetallesPorVenta/${idVenta}`)
            .then(response => response.json())
            .then(data => {
                //console.log(data); // Verifica la estructura de los datos
                const tableBody = document.querySelector("#detalleventa-table tbody");
                data.forEach(detalle => {
                        const row = document.createElement("tr");
                        row.innerHTML = `
                            <td>${detalle.id}</td>
                            <td>${detalle.cantidadVendida}</td>
                            <td>${detalle.articulo.nombreArticulo}</td>
                            <td>${idVenta}</td>
                    `;
                        tableBody.appendChild(row);
                });
            })
            .catch(error => {
                console.error("Error al obtener los detalles de venta:", error);
            });


});