document.addEventListener("DOMContentLoaded", function() {
    fetch("http://localhost:9090/api/v1/ordenescompras")
        .then(response => response.json())
        .then(data => {
            //console.log(data); // Verifica la estructura de los datos
            const tableBody = document.querySelector("#proveedores-table tbody");
            data.forEach(ordenesdecompras => {
                const row = document.createElement("tr");
                row.innerHTML = `
                <td>${ordenesdecompras.id}</td>
                <td>${ordenesdecompras.fechaOrdenCompra}</td>
                <td>${ordenesdecompras.estadoOrdenCompra}</td>
                <td>${ordenesdecompras.totalOrdenCompra}</td>
                <td>${articulo.proveedor ? ordenesdecompras.proveedor.nombreProveedor : 'No asignado'}</td>
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
        })
        .catch(error => {
            console.error("Error al obtener las ordenes de compra:", error);
        });
});