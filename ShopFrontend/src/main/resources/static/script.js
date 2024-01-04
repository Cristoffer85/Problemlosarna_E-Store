

function validatePassword() {
    let password = document.getElementsByName('password')[0].value;
    let passwordError = document.getElementById('passwordError');

    let passwordRegex = /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,}$/;

    if (!passwordRegex.test(password)) {
        passwordError.innerText = 'Password must contain at least one digit, one lowercase and one uppercase letter, and be at least 8 characters long.';
        return false;
    } else {
        passwordError.innerText = '';
        return true;
    }
}

function openPopup(productId) {
    let url = '/index/one/' + productId;
    window.open(url, 'Product Details', 'width=600,height=400');
}


function closeWindow() {
    window.close();
}

    <!-- onMouseOver/Out effekt för samtliga länkar i top nav-bar -->
document.addEventListener("DOMContentLoaded", function () {
    // Get all the text links in the top of the template
    const links = document.querySelectorAll('.navbar-nav a.nav-link');

    // Add event listeners for mouseover and mouseout
    links.forEach(function (link) {
        link.addEventListener('mouseover', function () {
            this.style.fontWeight = 'bold';
        });

        link.addEventListener('mouseout', function () {
            this.style.fontWeight = 'normal';
        });
    });
});