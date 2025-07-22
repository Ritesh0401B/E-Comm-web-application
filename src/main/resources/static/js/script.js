setTimeout(() => {

	const alertSuccess = document.querySelector('.alert-success');
	if (alertSuccess) {

		alertSuccess.remove();
	}

	const alertDanger = document.querySelector('.alert-danger');
	if (alertDanger) {

		alertDanger.remove();
	}

}, 4000);


// dropdown

document.querySelectorAll('.dropdown').forEach(function(dropdown) {
	dropdown.addEventListener('mouseover', function() {
		let menu = this.querySelector('.dropdown-menu');
		menu.classList.add('show');
	});

	dropdown.addEventListener('mouseout', function() {
		let menu = this.querySelector('.dropdown-menu');
		menu.classList.remove('show');
	});
});




//++++++++++++++++++++++++++ Password Visibility js +++++++++++++++++++++++++

function toggleVisibility(toggleId, inputId) {

	const toggle = document.getElementById(toggleId);

	const input = document.getElementById(inputId);

	if (toggle && input) {
		toggle.addEventListener("click", () => {


			if (input.type === "password") {
				input.type = "text"; // show password

				toggle.classList.add("fa-eye");
				toggle.classList.remove("fa-eye-slash");

			} else {
				input.type = "password";

				toggle.classList.add("fa-eye-slash");
				toggle.classList.remove("fa-eye");
			}

		});
	}

}

document.addEventListener("DOMContentLoaded", () => {
	toggleVisibility("toggleOld", "oldPassword");
	toggleVisibility("toggleNew", "newPassword");
	toggleVisibility("toggleConfirm", "confirmPassword");

	toggleVisibility("toggleLogin", "loginPassword");

	toggleVisibility("toggleSignup", "signupPassword");

	toggleVisibility("toggleConfirmSignup", "signupConfirmPassword")
});



// ++++++++++++++++++ Disable Button after First Click +++++++++++++++

function submitOrder() {
	const btn = document.getElementById("placeOrderBtn");
	btn.disabled = true;
	btn.innerText = "Processing...";


}





