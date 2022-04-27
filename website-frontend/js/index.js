function searchWallet() {
    let temp = document.createElement('a');
    let input = document.getElementById("wallet").value;
    alert(input);
    temp.href = "wallet.html?address=" + input;
    temp.click();

    update()
    setInterval(update, 30000);
}