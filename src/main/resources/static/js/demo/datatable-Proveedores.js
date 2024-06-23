document.addEventListener("DOMContentLoaded", function() {
    fetch("http://localhost:9090/api/v1/proveedores")
        .then(response => response.json())
        .then(data => {
            //console.log(data); // Verifica la estructura de los datos
            const tableBody = document.querySelector("#proveedores-table tbody");
            data.forEach(proveedor => {
                const row = document.createElement("tr");
                row.innerHTML = `
                <td>${proveedor.id}</td>
                <td>${proveedor.nombreProveedor}</td>
            `;
                tableBody.appendChild(row);
            });
        })
        .catch(error => {
            console.error("Error al obtener los proveedores:", error);
        });
});