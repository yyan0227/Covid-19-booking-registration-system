
window.addEventListener('load', () => {
    const logoutBtnId = "logoutBtn";
    const logoutBtn = document.getElementById(logoutBtnId);
    logoutBtn.addEventListener('click', () => {
        this.window.localStorage.removeItem("jwt");;
        window.location = "/login";
    });
    
    if (jwt !== null && jwt.length > 0) {
        console.log("jwt is not empty");
        this.window.localStorage.setItem("jwt", jwt);
    }
    else {
        console.log("jwt is null");
    }
});

