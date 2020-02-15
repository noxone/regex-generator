package org.olafneumann.models

data class Contact(val name: String, val phone: String, val email: String, val address: String) {
    companion object {
        fun all() = listOf(Contact("Paul Black", "+39 334256789", "paul.black@example.com", "282 Kevin Brook Street, Imogeneborough"),
                           Contact("John Red", "+44 340556677", "john.red@example.com", "3316 Arron Smith Drive, New Roads"),
                           Contact("Ken White", "+32 39876544", "ken.white@example.com", "169 Ersel Street, Paxtonville"))
    }
}
