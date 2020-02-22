package org.olafneumann.regex.generator.views.html

import org.olafneumann.regex.generator.models.Contact
import org.olafneumann.regex.generator.views.javafx.HTML5View

fun contactsView(contacts: List<Contact>) =
"""
<html>
    <head>
        <link rel="stylesheet" href="${HTML5View.resourceLink("/css/bootstrap.min.css")}" />
        <link rel="stylesheet" href="${HTML5View.resourceLink("/css/contacts.css")}" />
        <title>My contacts</title>
    </head>
    <body>
        <div class="container">
            <div class="row">
                <div class="col-md-6 col-md-offset-3">
                    ${contacts.map { contactPanel(it) }.joinToString("\n")}
                </div>
            </div>
        </div>
    </body>
</html>
"""

fun contactPanel(contact: Contact) =
"""
<div class="contact panel panel-info">
    <div class="panel-heading"> <h3 class="panel-title">${contact.name}</h3> </div>
    <div class="panel-body">
        <div class="row">
            <div class="col-md-3 col-lg-3" align="center">
                <img class="img-circle img-responsive"
                     alt="${contact.name} pic"
                     src="${HTML5View.resourceLink("/img/default-avatar.jpg")}" />
            </div>
            <div class="col-md-9 col-lg-9">
                <table class="table table-contact-information">
                    <tbody>
                        <tr> <td>Phone:</td> <td>${contact.phone}</td> </tr>
                        <tr> <td>Email:</td> <td>${contact.email}</td> </tr>
                        <tr> <td>Address:</td> <td>${contact.address}</td> </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>
"""