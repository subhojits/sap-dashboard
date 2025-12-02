// ===== AUTO-REFRESH DASHBOARD =====
// Uncomment to auto-refresh every 30 seconds
setInterval(() => {
    // location.reload();
}, 30000);

// ===== FORMAT NUMBERS WITH COMMAS =====
function formatNumber(num) {
    return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

// ===== SHOW NOTIFICATION =====
function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.innerText = message;
    document.body.appendChild(notification);

    setTimeout(() => notification.remove(), 3000);
}

// ===== EXPORT TABLE TO CSV =====
function exportTableToCSV(filename) {
    const table = document.querySelector('.events-table');
    let csv = [];

    for (let row of table.rows) {
        let csvRow = [];
        for (let cell of row.cells) {
            csvRow.push('"' + cell.innerText + '"');
        }
        csv.push(csvRow.join(','));
    }

    downloadCSV(csv.join('\n'), filename);
}

// ===== DOWNLOAD CSV FILE =====
function downloadCSV(csv, filename) {
    const csvFile = new Blob([csv], { type: 'text/csv' });
    const downloadLink = document.createElement('a');
    downloadLink.href = URL.createObjectURL(csvFile);
    downloadLink.download = filename || 'export.csv';
    downloadLink.click();
}
