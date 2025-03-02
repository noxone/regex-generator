//
//  main.c
//  CRegexTest
//
//  Created by Olaf Neumann on 15.01.22.
//

#include <stdio.h>
#include <regex.h>
#include <string.h>

int useRegex(const char* textToCheck) {
    regex_t compiledRegex;
    int reti;
    int actualReturnValue = -1;
    char messageBuffer[100];

    /* Compile regular expression */
    reti = regcomp(&compiledRegex, "^asd[0-9]+asd", REG_EXTENDED | REG_ICASE);
    if (reti) {
        fprintf(stderr, "Could not compile regex\n");
        return -2;
    }

    /* Execute compiled regular expression */
    reti = regexec(&compiledRegex, textToCheck, 0, NULL, 0);
    if (!reti) {
        puts("Match");
        actualReturnValue = 0;
    } else if (reti == REG_NOMATCH) {
        puts("No match");
        actualReturnValue = 1;
    } else {
        regerror(reti, &compiledRegex, messageBuffer, sizeof(messageBuffer));
        fprintf(stderr, "Regex match failed: %s\n", messageBuffer);
        actualReturnValue = -3;
    }

    /* Free memory allocated to the pattern buffer by regcomp() */
    regfree(&compiledRegex);
    return actualReturnValue;
}

int main(int argc, const char * argv[]) {
    useRegex("asd123asd");
    useRegex("Wohnung");
    useRegex("asd1233454534asd");
    useRegex("abc123asd");
    useRegex("ASD123asd");
    
    return 0;
}
