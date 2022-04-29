function searchWallet() {
    let temp = document.createElement('a');
    let input = document.getElementById("wallet").value;
    temp.href = "wallet.html?address=" + input;
    temp.click();
}

function searchTransaction() {
    let temp = document.createElement('a');
    let input = document.getElementById('transaction').value;
    temp.href = "transaction.html?hash=" + input;
    temp.click();
}

function searchBlock() {
    let temp = document.createElement('a');
    let input = document.getElementById('block').value;
    temp.href = "block.html?id=" + input;
    temp.click();
}