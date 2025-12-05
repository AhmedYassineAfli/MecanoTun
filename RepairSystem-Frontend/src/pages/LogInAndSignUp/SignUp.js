import axios from 'axios'
var config = require('../../../config')

var frontendUrl = 'http://' + config.dev.host + ':' + config.dev.port
var backendUrl = config.dev.backendHost

var AXIOS = axios.create({
	baseURL: backendUrl,
	headers: { 'Access-Control-Allow-Origin': frontendUrl }
})

function CustomerDto(email, password, name, phone, address) {
	this.name = name;
	this.password = password;
	this.phone = phone;
	this.email = email;
	this.address = address;

	this.appointments = "";
	this.id = "";
	this.cars = "";
	this.lastActive = "";
}

export default {
	name: "SignUp",
	data() {
		return {
			customer: "",
			error: "",
			newEmail: "",
			newPassword: "",
			newRepeatPassword: "",
			newName: "",
			newNumber: "",
			newAddress: ""
		}
	},
	methods: {
		createCustomer: function (email, password, name, phone, address) {
			AXIOS.post('/customer/'.concat(name), {},
				{
					params: {
						password: password,
						phone: phone,
						email: email,
						address: address
					}
				})
				.then(response => {
					this.customer = response.data;
					location.replace(frontendUrl + "/customerDashboard/Overview/" + this.customer.id);
				})
				.catch(e => {
					this.error = e
				})
		}
	}
}
