import os

def display_login():
    while True:
        print("Login:")
        print("1. Login as Student")
        print("2. Login as Advisor")
        print("3. Login as Administrator")
        print("4. Exit")
        choice = input("Enter your choice (1-4): ")

        try:
            choice = int(choice)
            if choice == 1:
                os.system('clear')
                print("You chose to login as a Student.\nChecking credentials...\n")
                input("Credentials verified. Welcome, Student X!\n Press enter to continue...")
                os.system('clear')
                student_home()
            elif choice == 2:
                os.system('clear')
                print("You chose to login as an Advisor.\nChecking credentials...\n")
                input("Credentials verified. Welcome, Advisor X!\n Press enter to continue...")
                os.system('clear')
                advisor_home()
            elif choice == 3:
                os.system('clear')
                print("You chose to login as an Administrator.\nChecking credentials...\n")
                input("Credentials verified. Welcome, Administrator X!\n Press enter to continue...")
                os.system('clear')
                admin_home()
            elif choice == 4:
                break
            else:
                print("Invalid choice. Please try again.")
        except ValueError:        
            print("Invalid input. Please enter a number.")
        enter()
  
def enter():
    input("Press enter to continue...")
    os.system('clear')

# Student menu options
def student_home():
    while True:
        print("Student Menu:")
        print("1. My Profile")
        print("2. My Degree")
        print("3. Registration")
        print("4. Logout")
        choice = input("Enter your choice (1-4): ")

        try:
            choice = int(choice)
            if choice == 1:
                manage_profile()
            elif choice == 2:
                manage_degree()
            elif choice == 3:
                manage_registration()
            elif choice == 4:
                input("\nLogging out...\nPress enter to continue...")
                exit()
            else:
                print("Invalid choice. Please try again.")
        except ValueError:        
            print("Invalid input. Please enter a number.")
        enter()

# Advisor menu options
def advisor_home():
    while True:
        print("Student Advisor Menu:")
        print("1. Access Student Profiles")
        print("2. Manage Meetings")
        print("3. Review Student Recommendations")
        print("4. Generate Reports")
        print("5. Logout")
        choice = input("Enter your choice (1-5): ")

        try:
            choice = int(choice)
            if choice == 1:
                print("Access Student Profiles selected.")
            elif choice == 2:
                print("Manage Meetings selected.")
            elif choice == 3:
                print("Review Student Recommendations selected.")
            elif choice == 4:
                generate_reports()
            elif choice == 5:
                input("\nLogging out...\nPress enter to continue...")
                exit()
            else:
                print("Invalid choice. Please try again.")
        except ValueError:        
            print("Invalid input. Please enter a number.")
        enter()

def admin_home():
    while True:
        print("Administrator Menu:")
        print("1. Configure System")
        print("2. View Logs")
        print("3. Manage Users")
        print("4. Generate Reports")
        print("5. Logout")
        choice = input("Enter your choice (1-5): ")

        try:
            choice = int(choice)
            if choice == 1:
                print("Configure System selected.")
            elif choice == 2:
                print("View Logs selected.")
            elif choice == 3:
                manage_users()
            elif choice == 4:
                generate_reports()
            elif choice == 5:
                input("\nLogging out...\nPress enter to continue...")
                exit()
            else:
                print("Invalid choice. Please try again.")
        except ValueError:        
            print("Invalid input. Please enter a number.")
        enter()

# Student exclusive sub-menu options
def manage_profile():
    print("You selected My Profile.")
    enter()
    while True:
        print("Student Profile Menu:")
        print("1. Edit Profile")
        print("2. Upload Documents")
        print("3. View Notifications")
        print("4. Set Goals")
        print("5. Go Back")
        choice = input("Enter your choice (1-5): ")

        try:
            choice = int(choice)
            if choice == 1:
                print("Edit profile selected.")
            elif choice == 2:
                print("Upload documents selected.")
            elif choice == 3:
                print("View notifications selected.")
            elif choice == 4:
                print("Set goals selected")
            elif choice == 5:
                print("Returning to Student Menu...")
                break
            else:
                print("Invalid choice. Please try again.")
        except ValueError:        
            print("Invalid input. Please enter a number.")
        enter()
    
def manage_degree():
    print("You selected My Degree.")
    enter()
    while True:
        print("Student Degree Menu:")
        print("1. View Degree Progress")
        print("2. View Degree Recommendations")
        print("3. View Degree Requirements")
        print("4. Go Back")
        choice = input("Enter your choice (1-4): ")

        try:
            choice = int(choice)
            if choice == 1:
                print("View Degree Progress selected.")
            elif choice == 2:
                print("View Degree Recommendations selected.")
            elif choice == 3:
                print("View Degree Requirements selected.")
            elif choice == 4:
                print("Returning to Student Menu...")
                break
            else:
                print("Invalid choice. Please try again.")
        except ValueError:        
            print("Invalid input. Please enter a number.")
        enter()

def manage_registration():
    print("You selected Registration.")
    enter()
    while True:
        print("Student Degree Menu:")
        print("1. Register for Courses")
        print("2. Search for Courses")
        print("3. View Course details")
        print("4. Go Back")
        choice = input("Enter your choice (1-4): ")

        try:
            choice = int(choice)
            if choice == 1:
                print("Register for Courses selected.")
            elif choice == 2:
                print("Search for Courses selected.")
            elif choice == 3:
                print("View Course details selected")
            elif choice == 4:
                print("Returning to Student Menu...")
                break
            else:
                print("Invalid choice. Please try again.")
        except ValueError:        
            print("Invalid input. Please enter a number.")
        enter()

# Admin exclusive sub-menu options
def manage_users():
    print("Manage Users selected.")
    return

# Admin/Advisor shared sub-menu options
def generate_reports():
    print("Generate Reports selected.")
    return

def main():
    os.system('clear')
    display_login()
    exit()

if __name__ == "__main__":
  main()
