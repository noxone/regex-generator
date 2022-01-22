//
//  main.c
//  CRegexTest
//
//  Created by Olaf Neumann on 15.01.22.
//

#include <stdio.h>
#include <regex.h>
#include "/opt/homebrew/Cellar/pcre/8.45/include/pcre.h"
#include <string.h>

int usePcre(char* textToCheck) {
    /* for pcre_compile */
    pcre* regex;
    const char* error;
    int erroffset;

    /* for pcre_exec */
    int rc;
    int ovector[30];

    /* to get substrings from regex */
    int rc2;
    const char *substring;
    
    /* return value */
    int returnValue = -1;

    regex = pcre_compile("regex", 0, &error, &erroffset, NULL);

    rc = pcre_exec(regex, NULL, textToCheck, (int)strlen(textToCheck), 0, 0, ovector, 30);

    
    if(rc == PCRE_ERROR_NOMATCH) {
        fprintf(stderr,"no match\n");
        returnValue = 0;
    } else if(rc < -1) {
        fprintf(stderr,"error %d from regex\n",rc);
        returnValue = -2;
    } else {
        returnValue = 1;
    }
    pcre_free(regex);

    return returnValue;
}

int useRegex(char* textToCheck) {
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

void check() {
    regex_t regex;
    int reti;
    char msgbuf[100];

    /* Compile regular expression */
    reti = regcomp(&regex, "asd[0-9]asd+", REG_EXTENDED);
    if (reti) {
        fprintf(stderr, "Could not compile regex\n");
        return;
    }

    /* Execute regular expression */
    reti = regexec(&regex, "asd", 0, NULL, 0);
    if (!reti) {
        puts("Match");
    }
    else if (reti == REG_NOMATCH) {
        puts("No match");
    }
    else {
        regerror(reti, &regex, msgbuf, sizeof(msgbuf));
        fprintf(stderr, "Regex match failed: %s\n", msgbuf);
    }

    /* Free memory allocated to the pattern buffer by regcomp() */
    regfree(&regex);
}

int main(int argc, const char * argv[]) {
    // insert code here...
    printf("Hello, Selina!\n");
    
    //check();
    
    useRegex("asd123asd");
    useRegex("Wohnung");
    useRegex("asd1233454534asd");
    useRegex("abc123asd");
    useRegex("ASD123asd");
    
    return 0;
}
