.. _admin:

|adminIcon| Admin
=================

.. |adminIcon| image:: img/baseline_settings_black_18dp.png 
   :width: 20

The **Admin** page is only visible if you have **Admin** permissions which can only be granted by another administrator.

Users
-----

This table manages permissions for users, adding new users and editing user information.

Answer checks for permissions throughout the website and a page can be entirely blocked if a user lacks the right permissions
or some features might be disabled or hidden.

A user without **View** permission cannot see any page on the website.

Removing all permissions is a way to disable a user from Answer. All annotations from that user will remain but she will not be able to use the website.

To verify a user's credentials, Answer will send the user ID or the email address to LDAP depending on what the user chooses to use on the login page. 
**Make sure that both pieces of information are correct when creating a new account.**

The first and last names are used in email communications and when displaying 
the author of an annotation or of a clinical report.
   
Groups
------

Groups handle access to each case. Not only does a user need to have the right permissions to access a case, the user and the case also need to be in the same group.

The concept of a group can be thought as an organization or a lab within a bigger organization. Users and cases are assigned to groups.

The **Group** table works similarly to the **Users** table. You can add and edit groups and assign users to those groups.

To assign groups to cases, admins have and extra button in the action column of the :ref:`homepage` page.


Gene Sets
---------

Gene sets are mainly used for variant filtering when working on a case. Users can create their own gene sets in the :ref:`annotations` page.

Some sets like **ACMG SF v2.0** have special functions and cannot be deleted because they are required for variant reporting. 
For now the only way to create a required gene sets is to set the **required** flag in the database.



