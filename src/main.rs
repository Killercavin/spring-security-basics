use std::io::{self, Write};

fn main() {
    println!("[ Guess Game ]");
    print!("Please enter a number: ");
    io::stdout().flush().unwrap(); // Ensure the prompt is printed before reading input

    let mut input = String::new();
    io::stdin().read_line(&mut input).expect("Failed to read line");

    let guessed_number: i32 = match input.trim().parse() {
        Ok(num) => num,
        Err(_) => {
            println!("The stuff you've entered doesn't look like an integer, Baka!");
            return;
        }
    };

    if guessed_number < 0 {
        println!("The number entered is negative hence very low!!");
    } else if guessed_number >= 0 && guessed_number <= 1000 {
        println!("The number entered is medium!!");
    } else {
        println!("The number entered is high!!");
    }
}