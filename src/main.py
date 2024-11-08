print("[ Guess Game ]")
try:
    guessed_number = int(input("Enter a number: "))
    if guessed_number < 0:
        print("The number entered is negative hence very low!!")
    elif guessed_number >= 0 and guessed_number <= 1000:
        print("The number entered is medium!!")
    else:
        print("The number entered is high!!")
except ValueError:
    print("The stuff entered is not an integer, Baka!")
    exit()