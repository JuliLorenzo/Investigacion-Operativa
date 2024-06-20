document.addEventListener("DOMContentLoaded", function() {
    fetch("http://localhost:9090/api/v1/ordenescompras")
        .then(response => response.json())
        .then(data => {
            const tableBody = document.querySelector("#ordenesdecompra-table tbody");
            data.forEach(ordenesdecompras => {
                const row = document.createElement("tr");
                row.innerHTML = `
                <td>${ordenesdecompras.id}</td>
                <td>${ordenesdecompras.articulo ? ordenesdecompras.articulo.nombreArticulo : 'No asignado'}</td>
                <td>${new Date(ordenesdecompras.fechaOrdenCompra).toLocaleString()}</td>
                <td>${ordenesdecompras.estadoOrdenCompra}</td>
                <td>${ordenesdecompras.totalOrdenCompra}</td>
                <td>${ordenesdecompras.proveedor ? ordenesdecompras.proveedor.nombreProveedor : 'No asignado'}</td>
                <td>
                    <button class="btn btn-primary btn-sm btn-detalle" data-id="${ordenesdecompras.id}">
                        Detalle
                    </button>
                </td>
                <td>
                    <div style="text-align: center">
                        <a href="#" class="btn btn-info btn-circle btn-sm" data-id="${ordenesdecompras.id}">
                            <i class="fas fa-link"></i>
                        </a>
                        <a href="#" class="btn btn-warning btn-circle btn-sm" data-id="${ordenesdecompras.id}">
                            <i class="fas fa-edit"></i>
                        </a>
                    </div>
                </td>
            `;
                tableBody.appendChild(row);
            });

            // Agregar evento click al botón de detalle
            tableBody.addEventListener('click', function(event) {
                if (event.target.classList.contains('btn-detalle')) {
                    const ordenCompraId = event.target.getAttribute('data-id');
                    // Redirigir a OrdenCompraDetalle.html con el ID de la orden de compra
                    window.location.href = `OrdenCompraDetalle.html?id=${ordenCompraId}`;
                }
            });
        })
        .catch(error => {
            console.error("Error al obtener las órdenes de compra:", error);
        });
});
