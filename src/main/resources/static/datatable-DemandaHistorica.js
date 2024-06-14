document.addEventListener("DOMContentLoaded", function() {
    fetch("http://localhost:9090/api/v1/demandashistoricas")
        .then(response => response.json())
        .then(data => {
            //console.log(data); // Verifica la estructura de los datos <td>${demandashistoricas.ventaList ? demandashistoricas.ventaList.map(venta => venta.detalle).join(', ') : 'N/A'}</td>
            const tableBody = document.querySelector("#demandahistorica-table tbody");
            data.forEach(demandashistoricas => {
                const row = document.createElement("tr");
                row.innerHTML = `
                <td>${demandashistoricas.id}</td>
                <td>${demandashistoricas.fechaDesde}</td>
                <td>${demandashistoricas.fechaHasta}</td>
                <td>${demandashistoricas.cantidadVendida}</td>
                <td>${demandashistoricas.articulo ? demandashistoricas.articulo.nombre : 'N/A'}</td>             
                <td>
                    <div style="text-align: center">
                        <a href="#" class="btn btn-info btn-circle btn-sm" data-id="${demandashistoricas.id}">
                            <i class="fas fa-link"></i>
                        </a>
                        <a href="#" class="btn btn-warning btn-circle btn-sm" data-id="${demandashistoricas.id}">
                            <i class="fas fa-edit"></i>
                        </a>
                    </div>
                </td>
            `;
                tableBody.appendChild(row);
            });
        })
        .catch(error => {
            console.error("Error al obtener los proveedores:", error);
        });
});