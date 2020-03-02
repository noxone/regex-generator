if (typeof kotlin === 'undefined') {
  throw new Error("Error loading module 'regex-generator-web'. Its dependency 'kotlin' was not found. Please, check whether 'kotlin' is loaded prior to 'regex-generator-web'.");
}
if (typeof this['kotlinx-html-js'] === 'undefined') {
  throw new Error("Error loading module 'regex-generator-web'. Its dependency 'kotlinx-html-js' was not found. Please, check whether 'kotlinx-html-js' is loaded prior to 'regex-generator-web'.");
}
this['regex-generator-web'] = function (_, Kotlin, $module$kotlinx_html_js) {
  'use strict';
  var $$importsForInline$$ = _.$$importsForInline$$ || (_.$$importsForInline$$ = {});
  var Kind_CLASS = Kotlin.Kind.CLASS;
  var NotImplementedError_init = Kotlin.kotlin.NotImplementedError;
  var listOf = Kotlin.kotlin.collections.listOf_i5x0yv$;
  var Kind_OBJECT = Kotlin.Kind.OBJECT;
  var replace = Kotlin.kotlin.text.replace_680rmw$;
  var Kind_INTERFACE = Kotlin.Kind.INTERFACE;
  var emptyList = Kotlin.kotlin.collections.emptyList_287e2$;
  var joinToString = Kotlin.kotlin.collections.joinToString_fmv235$;
  var trimMargin = Kotlin.kotlin.text.trimMargin_rjktp$;
  var Regex_init = Kotlin.kotlin.text.Regex_init_61zpoe$;
  var listOf_0 = Kotlin.kotlin.collections.listOf_mh5how$;
  var sortedWith = Kotlin.kotlin.collections.sortedWith_eknfly$;
  var wrapFunction = Kotlin.wrapFunction;
  var Comparator = Kotlin.kotlin.Comparator;
  var ArrayList_init = Kotlin.kotlin.collections.ArrayList_init_287e2$;
  var intersect = Kotlin.kotlin.collections.intersect_q4559j$;
  var IntRange = Kotlin.kotlin.ranges.IntRange;
  var addAll = Kotlin.kotlin.collections.addAll_ipc267$;
  var mutableListOf = Kotlin.kotlin.collections.mutableListOf_i5x0yv$;
  var until = Kotlin.kotlin.ranges.until_dqglrj$;
  var step = Kotlin.kotlin.ranges.step_xsgg7u$;
  var to = Kotlin.kotlin.to_ujzrz7$;
  var lazy = Kotlin.kotlin.lazy_klfg04$;
  var collectionSizeOrDefault = Kotlin.kotlin.collections.collectionSizeOrDefault_ba2ldo$;
  var ArrayList_init_0 = Kotlin.kotlin.collections.ArrayList_init_ww73n8$;
  var UnsupportedOperationException_init = Kotlin.kotlin.UnsupportedOperationException_init_pdl1vj$;
  var checkIndexOverflow = Kotlin.kotlin.collections.checkIndexOverflow_za3lpa$;
  var map = Kotlin.kotlin.sequences.map_z5avom$;
  var toList = Kotlin.kotlin.sequences.toList_veqyi0$;
  var MatchNamedGroupCollection = Kotlin.kotlin.text.MatchNamedGroupCollection;
  var throwCCE = Kotlin.throwCCE;
  var toString = Kotlin.toString;
  var Exception_init = Kotlin.kotlin.Exception_init_pdl1vj$;
  var RuntimeException_init = Kotlin.kotlin.RuntimeException_init_pdl1vj$;
  var indexOf = Kotlin.kotlin.text.indexOf_l5u8uk$;
  var IllegalArgumentException_init = Kotlin.kotlin.IllegalArgumentException_init_pdl1vj$;
  var isBlank = Kotlin.kotlin.text.isBlank_gw00vp$;
  var Exception = Kotlin.kotlin.Exception;
  var toBoolean = Kotlin.kotlin.text.toBoolean_pdl1vz$;
  var toIntOrNull = Kotlin.kotlin.text.toIntOrNull_pdl1vz$;
  var RuntimeException = Kotlin.kotlin.RuntimeException;
  var ClassCastException = Kotlin.kotlin.ClassCastException;
  var addClass = Kotlin.kotlin.dom.addClass_hhb33f$;
  var removeClass = Kotlin.kotlin.dom.removeClass_hhb33f$;
  var clear = Kotlin.kotlin.dom.clear_asww5s$;
  var max = Kotlin.kotlin.collections.max_exjks8$;
  var toList_0 = Kotlin.kotlin.collections.toList_7wnvza$;
  var ensureNotNull = Kotlin.ensureNotNull;
  var Unit = Kotlin.kotlin.Unit;
  var compareBy = Kotlin.kotlin.comparisons.compareBy_bvgy4j$;
  var toMap = Kotlin.kotlin.collections.toMap_6hr0sd$;
  var LinkedHashMap_init = Kotlin.kotlin.collections.LinkedHashMap_init_q3lmfv$;
  var get_create = $module$kotlinx_html_js.kotlinx.html.dom.get_create_4wc2mh$;
  var iterator = Kotlin.kotlin.text.iterator_gw00vp$;
  var unboxChar = Kotlin.unboxChar;
  var ButtonType = $module$kotlinx_html_js.kotlinx.html.ButtonType;
  var set_id = $module$kotlinx_html_js.kotlinx.html.set_id_ueiko3$;
  var attributesMapOf = $module$kotlinx_html_js.kotlinx.html.attributesMapOf_jyasbz$;
  var DIV_init = $module$kotlinx_html_js.kotlinx.html.DIV;
  var HTMLDivElement_0 = HTMLDivElement;
  var visitTagAndFinalize = $module$kotlinx_html_js.kotlinx.html.visitTagAndFinalize_g9qte5$;
  var P_init = $module$kotlinx_html_js.kotlinx.html.P;
  var StringBuilder_init = Kotlin.kotlin.text.StringBuilder_init_za3lpa$;
  var enumEncode = $module$kotlinx_html_js.kotlinx.html.attributes.enumEncode_m4whry$;
  var attributesMapOf_0 = $module$kotlinx_html_js.kotlinx.html.attributesMapOf_alerag$;
  var BUTTON_init = $module$kotlinx_html_js.kotlinx.html.BUTTON;
  var visitTag = $module$kotlinx_html_js.kotlinx.html.visitTag_xwv8ym$;
  var CODE_init = $module$kotlinx_html_js.kotlinx.html.CODE;
  var PRE_init = $module$kotlinx_html_js.kotlinx.html.PRE;
  var toSet = Kotlin.kotlin.collections.toSet_7wnvza$;
  var equals = Kotlin.equals;
  var Collection = Kotlin.kotlin.collections.Collection;
  JavaCodeGenerator.prototype = Object.create(SimpleReplacingCodeGenerator.prototype);
  JavaCodeGenerator.prototype.constructor = JavaCodeGenerator;
  KotlinCodeGenerator.prototype = Object.create(SimpleReplacingCodeGenerator.prototype);
  KotlinCodeGenerator.prototype.constructor = KotlinCodeGenerator;
  PhpCodeGenerator.prototype = Object.create(SimpleReplacingCodeGenerator.prototype);
  PhpCodeGenerator.prototype.constructor = PhpCodeGenerator;
  JavaScriptCodeGenerator.prototype = Object.create(SimpleReplacingCodeGenerator.prototype);
  JavaScriptCodeGenerator.prototype.constructor = JavaScriptCodeGenerator;
  CSharpCodeGenerator.prototype = Object.create(SimpleReplacingCodeGenerator.prototype);
  CSharpCodeGenerator.prototype.constructor = CSharpCodeGenerator;
  function createStepDefinition(element, title, description, position) {
    var stepDefinition = {};
    stepDefinition.element = element;
    stepDefinition.popover = {};
    stepDefinition.popover.title = title;
    stepDefinition.popover.description = description;
    stepDefinition.popover.position = position;
    return stepDefinition;
  }
  function jQuery(element) {
    return $('#' + element.id);
  }
  function BracketedRecognizer(name, outputPattern, description, active) {
    this.name_umq4d7$_0 = name;
    this.outputPattern_rzs6v3$_0 = outputPattern;
    this.description_ffks6q$_0 = description;
    this.active_hu82o0$_0 = active;
  }
  Object.defineProperty(BracketedRecognizer.prototype, 'name', {
    get: function () {
      return this.name_umq4d7$_0;
    }
  });
  Object.defineProperty(BracketedRecognizer.prototype, 'outputPattern', {
    get: function () {
      return this.outputPattern_rzs6v3$_0;
    }
  });
  Object.defineProperty(BracketedRecognizer.prototype, 'description', {
    get: function () {
      return this.description_ffks6q$_0;
    }
  });
  Object.defineProperty(BracketedRecognizer.prototype, 'active', {
    get: function () {
      return this.active_hu82o0$_0;
    }
  });
  BracketedRecognizer.prototype.findMatches_61zpoe$ = function (input) {
    throw new NotImplementedError_init('An operation is not implemented: ' + 'not implemented');
  };
  BracketedRecognizer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'BracketedRecognizer',
    interfaces: [Recognizer]
  };
  BracketedRecognizer.prototype.component1 = function () {
    return this.name;
  };
  BracketedRecognizer.prototype.component2 = function () {
    return this.outputPattern;
  };
  BracketedRecognizer.prototype.component3 = function () {
    return this.description;
  };
  BracketedRecognizer.prototype.component4 = function () {
    return this.active;
  };
  BracketedRecognizer.prototype.copy_ii6ut2$ = function (name, outputPattern, description, active) {
    return new BracketedRecognizer(name === void 0 ? this.name : name, outputPattern === void 0 ? this.outputPattern : outputPattern, description === void 0 ? this.description : description, active === void 0 ? this.active : active);
  };
  BracketedRecognizer.prototype.toString = function () {
    return 'BracketedRecognizer(name=' + Kotlin.toString(this.name) + (', outputPattern=' + Kotlin.toString(this.outputPattern)) + (', description=' + Kotlin.toString(this.description)) + (', active=' + Kotlin.toString(this.active)) + ')';
  };
  BracketedRecognizer.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.name) | 0;
    result = result * 31 + Kotlin.hashCode(this.outputPattern) | 0;
    result = result * 31 + Kotlin.hashCode(this.description) | 0;
    result = result * 31 + Kotlin.hashCode(this.active) | 0;
    return result;
  };
  BracketedRecognizer.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.name, other.name) && Kotlin.equals(this.outputPattern, other.outputPattern) && Kotlin.equals(this.description, other.description) && Kotlin.equals(this.active, other.active)))));
  };
  function Comparator$ObjectLiteral(closure$comparison) {
    this.closure$comparison = closure$comparison;
  }
  Comparator$ObjectLiteral.prototype.compare = function (a, b) {
    return this.closure$comparison(a, b);
  };
  Comparator$ObjectLiteral.$metadata$ = {kind: Kind_CLASS, interfaces: [Comparator]};
  var compareBy$lambda = wrapFunction(function () {
    var compareValues = Kotlin.kotlin.comparisons.compareValues_s00gnj$;
    return function (closure$selector) {
      return function (a, b) {
        var selector = closure$selector;
        return compareValues(selector(a), selector(b));
      };
    };
  });
  function CodeGenerator() {
    CodeGenerator$Companion_getInstance();
  }
  function CodeGenerator$Companion() {
    CodeGenerator$Companion_instance = this;
    this.list = sortedWith(listOf([new JavaCodeGenerator(), new KotlinCodeGenerator(), new PhpCodeGenerator(), new JavaScriptCodeGenerator(), new CSharpCodeGenerator()]), new Comparator$ObjectLiteral(compareBy$lambda(CodeGenerator$Companion$list$lambda)));
  }
  function CodeGenerator$Companion$list$lambda(it) {
    return it.languageName;
  }
  CodeGenerator$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var CodeGenerator$Companion_instance = null;
  function CodeGenerator$Companion_getInstance() {
    if (CodeGenerator$Companion_instance === null) {
      new CodeGenerator$Companion();
    }
    return CodeGenerator$Companion_instance;
  }
  Object.defineProperty(CodeGenerator.prototype, 'uniqueName', {
    get: function () {
      return replace(replace(replace(this.languageName, '-', '_minus_'), '+', '_plus_'), '#', '_sharp_');
    }
  });
  CodeGenerator.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'CodeGenerator',
    interfaces: []
  };
  function GeneratedSnippet(snippet, warnings) {
    if (warnings === void 0)
      warnings = emptyList();
    this.snippet = snippet;
    this.warnings = warnings;
  }
  GeneratedSnippet.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'GeneratedSnippet',
    interfaces: []
  };
  GeneratedSnippet.prototype.component1 = function () {
    return this.snippet;
  };
  GeneratedSnippet.prototype.component2 = function () {
    return this.warnings;
  };
  GeneratedSnippet.prototype.copy_kwv3np$ = function (snippet, warnings) {
    return new GeneratedSnippet(snippet === void 0 ? this.snippet : snippet, warnings === void 0 ? this.warnings : warnings);
  };
  GeneratedSnippet.prototype.toString = function () {
    return 'GeneratedSnippet(snippet=' + Kotlin.toString(this.snippet) + (', warnings=' + Kotlin.toString(this.warnings)) + ')';
  };
  GeneratedSnippet.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.snippet) | 0;
    result = result * 31 + Kotlin.hashCode(this.warnings) | 0;
    return result;
  };
  GeneratedSnippet.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.snippet, other.snippet) && Kotlin.equals(this.warnings, other.warnings)))));
  };
  function SimpleReplacingCodeGenerator() {
  }
  SimpleReplacingCodeGenerator.prototype.transformPattern_wa467u$ = function (pattern, options) {
    return pattern;
  };
  SimpleReplacingCodeGenerator.prototype.getWarnings_wa467u$ = function (pattern, options) {
    return emptyList();
  };
  SimpleReplacingCodeGenerator.prototype.generateCode_wa467u$ = function (pattern, options) {
    return new GeneratedSnippet(replace(replace(this.templateCode, '%1$s', this.transformPattern_wa467u$(pattern, options)), '%2$s', this.generateOptionsCode_ow7xd4$(options)), this.getWarnings_wa467u$(pattern, options));
  };
  function SimpleReplacingCodeGenerator$combineOptions$lambda(closure$mapper) {
    return function (s) {
      return closure$mapper(s);
    };
  }
  SimpleReplacingCodeGenerator.prototype.combineOptions_1rvtm9$$default = function (options, valueForCaseInsensitive, valueForMultiline, valueForDotAll, valueIfNone, prefix, separator, postfix, mapper) {
    var optionList = ArrayList_init();
    if (options.caseSensitive && valueForCaseInsensitive != null) {
      optionList.add_11rb$(valueForCaseInsensitive);
    }
    if (options.dotMatchesLineBreaks && valueForDotAll != null) {
      optionList.add_11rb$(valueForDotAll);
    }
    if (options.multiline && valueForMultiline != null) {
      optionList.add_11rb$(valueForMultiline);
    }
    if (optionList.isEmpty()) {
      return valueIfNone;
    }
    return joinToString(optionList, separator, prefix, postfix, void 0, void 0, SimpleReplacingCodeGenerator$combineOptions$lambda(mapper));
  };
  function SimpleReplacingCodeGenerator$combineOptions$lambda_0(s) {
    return s;
  }
  SimpleReplacingCodeGenerator.prototype.combineOptions_1rvtm9$ = function (options, valueForCaseInsensitive, valueForMultiline, valueForDotAll, valueIfNone, prefix, separator, postfix, mapper, callback$default) {
    if (valueForCaseInsensitive === void 0)
      valueForCaseInsensitive = null;
    if (valueForMultiline === void 0)
      valueForMultiline = null;
    if (valueForDotAll === void 0)
      valueForDotAll = null;
    if (valueIfNone === void 0)
      valueIfNone = '';
    if (prefix === void 0)
      prefix = '';
    if (separator === void 0)
      separator = '';
    if (postfix === void 0)
      postfix = '';
    if (mapper === void 0)
      mapper = SimpleReplacingCodeGenerator$combineOptions$lambda_0;
    return callback$default ? callback$default(options, valueForCaseInsensitive, valueForMultiline, valueForDotAll, valueIfNone, prefix, separator, postfix, mapper) : this.combineOptions_1rvtm9$$default(options, valueForCaseInsensitive, valueForMultiline, valueForDotAll, valueIfNone, prefix, separator, postfix, mapper);
  };
  SimpleReplacingCodeGenerator.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SimpleReplacingCodeGenerator',
    interfaces: [CodeGenerator]
  };
  function JavaCodeGenerator() {
    SimpleReplacingCodeGenerator.call(this);
  }
  Object.defineProperty(JavaCodeGenerator.prototype, 'languageName', {
    get: function () {
      return 'Java';
    }
  });
  Object.defineProperty(JavaCodeGenerator.prototype, 'highlightLanguage', {
    get: function () {
      return 'java';
    }
  });
  Object.defineProperty(JavaCodeGenerator.prototype, 'templateCode', {
    get: function () {
      return trimMargin('import java.util.regex.Pattern;\n                |import java.util.regex.Matcher;\n                |\n                |public class Sample {\n                |    public boolean useRegex(String input) {\n                |        // Compile regular expression\n                |        Pattern pattern = Pattern.compile("%1$s"%2$s);\n                |        // Match regex against input\n                |        Matcher matcher = pattern.matcher(input);\n                |        // Use results...\n                |        return matcher.matches();\n                |    }\n                |}');
    }
  });
  JavaCodeGenerator.prototype.transformPattern_wa467u$ = function (pattern, options) {
    var $receiver = Regex_init('([\\\\"])').replace_x2uqeu$(pattern, '\\$1');
    return Regex_init('\t').replace_x2uqeu$($receiver, '\\t');
  };
  function JavaCodeGenerator$generateOptionsCode$lambda(s) {
    return 'Pattern.' + s;
  }
  JavaCodeGenerator.prototype.generateOptionsCode_ow7xd4$ = function (options) {
    return this.combineOptions_1rvtm9$(options, 'CASE_INSENSITIVE', 'MULTILINE', 'DOTALL', void 0, ' ,', ' | ', void 0, JavaCodeGenerator$generateOptionsCode$lambda);
  };
  JavaCodeGenerator.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JavaCodeGenerator',
    interfaces: [SimpleReplacingCodeGenerator]
  };
  function KotlinCodeGenerator() {
    SimpleReplacingCodeGenerator.call(this);
  }
  Object.defineProperty(KotlinCodeGenerator.prototype, 'languageName', {
    get: function () {
      return 'Kotlin';
    }
  });
  Object.defineProperty(KotlinCodeGenerator.prototype, 'highlightLanguage', {
    get: function () {
      return 'kotlin';
    }
  });
  Object.defineProperty(KotlinCodeGenerator.prototype, 'templateCode', {
    get: function () {
      return 'fun useRegex(input: String): Boolean {\n    val regex = Regex(pattern = "%1$s"%2$s)\n    return regex.matches(input)\n}';
    }
  });
  KotlinCodeGenerator.prototype.transformPattern_wa467u$ = function (pattern, options) {
    var $receiver = Regex_init('([\\\\"])').replace_x2uqeu$(pattern, '\\$1');
    return Regex_init('\t').replace_x2uqeu$($receiver, '\\t');
  };
  function KotlinCodeGenerator$generateOptionsCode$lambda(s) {
    return 'RegexOption.' + s;
  }
  KotlinCodeGenerator.prototype.generateOptionsCode_ow7xd4$ = function (options) {
    return this.combineOptions_1rvtm9$(options, 'IGNORE_CASE', 'MULTILINE', 'DOT_MATCHES_ALL', void 0, ', options = setOf(', ', ', ')', KotlinCodeGenerator$generateOptionsCode$lambda);
  };
  KotlinCodeGenerator.prototype.getWarnings_wa467u$ = function (pattern, options) {
    if (options.dotMatchesLineBreaks)
      return listOf_0("The option 'RegexOption.DOT_MATCHES_ALL' is only supported on JVM runtime.");
    return emptyList();
  };
  KotlinCodeGenerator.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'KotlinCodeGenerator',
    interfaces: [SimpleReplacingCodeGenerator]
  };
  function PhpCodeGenerator() {
    SimpleReplacingCodeGenerator.call(this);
  }
  Object.defineProperty(PhpCodeGenerator.prototype, 'languageName', {
    get: function () {
      return 'PHP';
    }
  });
  Object.defineProperty(PhpCodeGenerator.prototype, 'highlightLanguage', {
    get: function () {
      return 'php';
    }
  });
  PhpCodeGenerator.prototype.transformPattern_wa467u$ = function (pattern, options) {
    var $receiver = Regex_init("([\\\\'])").replace_x2uqeu$(pattern, '\\$1');
    return Regex_init('\t').replace_x2uqeu$($receiver, '\\t');
  };
  Object.defineProperty(PhpCodeGenerator.prototype, 'templateCode', {
    get: function () {
      return "<?php\nfunction useRegex($input) {\n    $regex = '/%1$s/%2$s';\n    return preg_match($regex, $input);\n}\n?>";
    }
  });
  PhpCodeGenerator.prototype.generateOptionsCode_ow7xd4$ = function (options) {
    return this.combineOptions_1rvtm9$(options, 'i', 'm', 's');
  };
  PhpCodeGenerator.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'PhpCodeGenerator',
    interfaces: [SimpleReplacingCodeGenerator]
  };
  function JavaScriptCodeGenerator() {
    SimpleReplacingCodeGenerator.call(this);
  }
  Object.defineProperty(JavaScriptCodeGenerator.prototype, 'languageName', {
    get: function () {
      return 'JavaScript';
    }
  });
  Object.defineProperty(JavaScriptCodeGenerator.prototype, 'highlightLanguage', {
    get: function () {
      return 'javascript';
    }
  });
  Object.defineProperty(JavaScriptCodeGenerator.prototype, 'templateCode', {
    get: function () {
      return 'function useRegex(input) {\n    let regex = /%1$s/%2$s;\n    return regex.test(input);\n}';
    }
  });
  JavaScriptCodeGenerator.prototype.transformPattern_wa467u$ = function (pattern, options) {
    return Regex_init('\t').replace_x2uqeu$(pattern, '\\t');
  };
  JavaScriptCodeGenerator.prototype.generateOptionsCode_ow7xd4$ = function (options) {
    return this.combineOptions_1rvtm9$(options, 'i', 'm', 's');
  };
  JavaScriptCodeGenerator.prototype.getWarnings_wa467u$ = function (pattern, options) {
    if (options.dotMatchesLineBreaks)
      return listOf_0("The option 's' (dot matches line breaks) is not supported in Firefox and IE.");
    return emptyList();
  };
  JavaScriptCodeGenerator.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JavaScriptCodeGenerator',
    interfaces: [SimpleReplacingCodeGenerator]
  };
  function CSharpCodeGenerator() {
    SimpleReplacingCodeGenerator.call(this);
  }
  Object.defineProperty(CSharpCodeGenerator.prototype, 'languageName', {
    get: function () {
      return 'C#';
    }
  });
  Object.defineProperty(CSharpCodeGenerator.prototype, 'highlightLanguage', {
    get: function () {
      return 'csharp';
    }
  });
  Object.defineProperty(CSharpCodeGenerator.prototype, 'templateCode', {
    get: function () {
      return 'using System;\nusing System.Text.RegularExpressions;\n\npublic class Sample\n{\n    public static void useRegex(String input)\n    {\n        Regex regex = new Regex("%1$s"%2$s);\n        return regex.IsMatch(input);\n    }\n}';
    }
  });
  CSharpCodeGenerator.prototype.transformPattern_wa467u$ = function (pattern, options) {
    var $receiver = Regex_init('([\\\\"])').replace_x2uqeu$(pattern, '\\$1');
    return Regex_init('\t').replace_x2uqeu$($receiver, '\\t');
  };
  function CSharpCodeGenerator$generateOptionsCode$lambda(s) {
    return 'RegexOptions.' + s;
  }
  CSharpCodeGenerator.prototype.generateOptionsCode_ow7xd4$ = function (options) {
    return this.combineOptions_1rvtm9$(options, 'IgnoreCase', 'Multiline', 'Singleline', void 0, ', ', ' | ', void 0, CSharpCodeGenerator$generateOptionsCode$lambda);
  };
  CSharpCodeGenerator.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'CSharpCodeGenerator',
    interfaces: [SimpleReplacingCodeGenerator]
  };
  function Configuration(recognizers) {
    Configuration$Companion_getInstance();
    this.recognizers = recognizers;
  }
  function Configuration$Companion() {
    Configuration$Companion_instance = this;
    this.default = new Configuration(listOf([new SimpleRecognizer('number', '[0-9]+'), new SimpleRecognizer('date', '[0-9]{4}-[0-9]{2}-[0-9]{2}'), new SimpleRecognizer('real', '[0-9]*\\.[0-9]+'), new SimpleRecognizer('day', '(0?[1-9]|[12][0-9]|3[01])', void 0, void 0, '(?:^|\\D)(%s)($|\\D)'), new SimpleRecognizer('month', '(0?[1-9]|[1][0-2])', void 0, void 0, '(?:^|\\D)(%s)($|\\D)'), new SimpleRecognizer('time', '[0-9]{2}:[0-9]{2}:[0-9]{2}(\\.[0-9]{1,3})?'), new SimpleRecognizer('ISO8601', '[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}(\\.[0-9]+)?([zZ]|([\\+-])([01]\\d|2[0-3]):?([0-5]\\d)?)?'), new SimpleRecognizer('String 1', "'([^']|\\\\')*'"), new SimpleRecognizer('String 2', '"([^"]|\\\\\')*"'), new SimpleRecognizer('Hashtag', "\\B#([a-z0-9]{2,})(?![~!@#$%^&*()=+_`\\-\\|\\/'\\[\\]\\{\\}]|[?.,]*\\w)"), new SimpleRecognizer('loglevel', '(TRACE|DEBUG|INFO|NOTICE|WARN|ERROR|SEVERE|FATAL)'), new SimpleRecognizer('Characters', '[a-zA-Z]+')]));
  }
  Configuration$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var Configuration$Companion_instance = null;
  function Configuration$Companion_getInstance() {
    if (Configuration$Companion_instance === null) {
      new Configuration$Companion();
    }
    return Configuration$Companion_instance;
  }
  Configuration.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Configuration',
    interfaces: []
  };
  Configuration.prototype.component1 = function () {
    return this.recognizers;
  };
  Configuration.prototype.copy_lde1di$ = function (recognizers) {
    return new Configuration(recognizers === void 0 ? this.recognizers : recognizers);
  };
  Configuration.prototype.toString = function () {
    return 'Configuration(recognizers=' + Kotlin.toString(this.recognizers) + ')';
  };
  Configuration.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.recognizers) | 0;
    return result;
  };
  Configuration.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && Kotlin.equals(this.recognizers, other.recognizers))));
  };
  function Recognizer() {
    Recognizer$Companion_getInstance();
  }
  function Recognizer$Companion() {
    Recognizer$Companion_instance = this;
  }
  Recognizer$Companion.prototype.recognize_mu52mh$ = function (input, config) {
    if (config === void 0)
      config = Configuration$Companion_getInstance().default;
    var $receiver = config.recognizers;
    var destination = ArrayList_init();
    var tmp$;
    tmp$ = $receiver.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      var list = element.findMatches_61zpoe$(input);
      addAll(destination, list);
    }
    return destination;
  };
  Recognizer$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var Recognizer$Companion_instance = null;
  function Recognizer$Companion_getInstance() {
    if (Recognizer$Companion_instance === null) {
      new Recognizer$Companion();
    }
    return Recognizer$Companion_instance;
  }
  Recognizer.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Recognizer',
    interfaces: []
  };
  function RecognizerMatch(startRange, mainRange, endRange, inputPart, recognizer) {
    if (startRange === void 0)
      startRange = null;
    if (endRange === void 0)
      endRange = null;
    this.startRange = startRange;
    this.mainRange = mainRange;
    this.endRange = endRange;
    this.inputPart = inputPart;
    this.recognizer = recognizer;
    var tmp$, tmp$_0, tmp$_1, tmp$_2;
    this.first = (tmp$_0 = (tmp$ = this.startRange) != null ? tmp$.first : null) != null ? tmp$_0 : this.mainRange.first;
    this.last = (tmp$_2 = (tmp$_1 = this.endRange) != null ? tmp$_1.last : null) != null ? tmp$_2 : this.mainRange.last;
    this.length = this.last - this.first + 1 | 0;
    this.totalRange_0 = new IntRange(this.first, this.last);
  }
  RecognizerMatch.prototype.intersect_hdji9c$ = function (other) {
    return !intersect(this.totalRange_0, other.totalRange_0).isEmpty();
  };
  RecognizerMatch.prototype.toString = function () {
    return '[' + this.first + '+' + this.length + '] (' + this.recognizer.name + ': ' + this.recognizer.outputPattern + ') ' + this.inputPart;
  };
  RecognizerMatch.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'RecognizerMatch',
    interfaces: []
  };
  RecognizerMatch.prototype.component1 = function () {
    return this.startRange;
  };
  RecognizerMatch.prototype.component2 = function () {
    return this.mainRange;
  };
  RecognizerMatch.prototype.component3 = function () {
    return this.endRange;
  };
  RecognizerMatch.prototype.component4 = function () {
    return this.inputPart;
  };
  RecognizerMatch.prototype.component5 = function () {
    return this.recognizer;
  };
  RecognizerMatch.prototype.copy_hsopiu$ = function (startRange, mainRange, endRange, inputPart, recognizer) {
    return new RecognizerMatch(startRange === void 0 ? this.startRange : startRange, mainRange === void 0 ? this.mainRange : mainRange, endRange === void 0 ? this.endRange : endRange, inputPart === void 0 ? this.inputPart : inputPart, recognizer === void 0 ? this.recognizer : recognizer);
  };
  RecognizerMatch.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.startRange) | 0;
    result = result * 31 + Kotlin.hashCode(this.mainRange) | 0;
    result = result * 31 + Kotlin.hashCode(this.endRange) | 0;
    result = result * 31 + Kotlin.hashCode(this.inputPart) | 0;
    result = result * 31 + Kotlin.hashCode(this.recognizer) | 0;
    return result;
  };
  RecognizerMatch.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.startRange, other.startRange) && Kotlin.equals(this.mainRange, other.mainRange) && Kotlin.equals(this.endRange, other.endRange) && Kotlin.equals(this.inputPart, other.inputPart) && Kotlin.equals(this.recognizer, other.recognizer)))));
  };
  function Comparator$ObjectLiteral_0(closure$comparison) {
    this.closure$comparison = closure$comparison;
  }
  Comparator$ObjectLiteral_0.prototype.compare = function (a, b) {
    return this.closure$comparison(a, b);
  };
  Comparator$ObjectLiteral_0.$metadata$ = {kind: Kind_CLASS, interfaces: [Comparator]};
  var compareBy$lambda_0 = wrapFunction(function () {
    var compareValues = Kotlin.kotlin.comparisons.compareValues_s00gnj$;
    return function (closure$selector) {
      return function (a, b) {
        var selector = closure$selector;
        return compareValues(selector(a), selector(b));
      };
    };
  });
  function RecognizerCombiner() {
    RecognizerCombiner$Companion_getInstance();
  }
  function RecognizerCombiner$Companion() {
    RecognizerCombiner$Companion_instance = this;
  }
  function RecognizerCombiner$Companion$combine$lambda(it) {
    return it.first;
  }
  RecognizerCombiner$Companion.prototype.combine_9yx9zl$ = function (inputText, selectedMatches, options) {
    if (options === void 0)
      options = new RecognizerCombiner$Options();
    var orderedMatches = sortedWith(selectedMatches, new Comparator$ObjectLiteral_0(compareBy$lambda_0(RecognizerCombiner$Companion$combine$lambda)));
    var indices = mutableListOf([0]);
    var destination = ArrayList_init();
    var tmp$;
    tmp$ = orderedMatches.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      var list = listOf([element.first, element.last + 1 | 0]);
      addAll(destination, list);
    }
    indices.addAll_brywnq$(destination);
    indices.add_11rb$(inputText.length);
    var $receiver = step(until(0, indices.size), 2);
    var destination_0 = ArrayList_init_0(collectionSizeOrDefault($receiver, 10));
    var tmp$_0;
    tmp$_0 = $receiver.iterator();
    while (tmp$_0.hasNext()) {
      var item = tmp$_0.next();
      destination_0.add_11rb$(to(indices.get_za3lpa$(item), indices.get_za3lpa$(item + 1 | 0)));
    }
    var destination_1 = ArrayList_init_0(collectionSizeOrDefault(destination_0, 10));
    var tmp$_1;
    tmp$_1 = destination_0.iterator();
    while (tmp$_1.hasNext()) {
      var item_0 = tmp$_1.next();
      var tmp$_2 = destination_1.add_11rb$;
      var startIndex = item_0.first;
      var endIndex = item_0.second;
      tmp$_2.call(destination_1, inputText.substring(startIndex, endIndex));
    }
    var destination_2 = ArrayList_init_0(collectionSizeOrDefault(destination_1, 10));
    var tmp$_3;
    tmp$_3 = destination_1.iterator();
    while (tmp$_3.hasNext()) {
      var item_1 = tmp$_3.next();
      destination_2.add_11rb$(this.escapeRegex_0(item_1));
    }
    var destination_3 = ArrayList_init_0(collectionSizeOrDefault(destination_2, 10));
    var tmp$_4;
    tmp$_4 = destination_2.iterator();
    while (tmp$_4.hasNext()) {
      var item_2 = tmp$_4.next();
      var tmp$_5 = destination_3.add_11rb$;
      var tmp$_6 = options.onlyPatterns;
      if (tmp$_6) {
        tmp$_6 = item_2.length > 0;
      }
      tmp$_5.call(destination_3, tmp$_6 ? '.*' : item_2);
    }
    var staticValues = destination_3;
    var tmp$_7;
    var iterator = staticValues.iterator();
    if (!iterator.hasNext())
      throw UnsupportedOperationException_init("Empty collection can't be reduced.");
    var index = 1;
    var accumulator = iterator.next();
    while (iterator.hasNext()) {
      var index_0 = checkIndexOverflow((tmp$_7 = index, index = tmp$_7 + 1 | 0, tmp$_7));
      var acc = accumulator;
      var s = iterator.next();
      accumulator = acc + ((index_0 - 1 | 0) < orderedMatches.size ? orderedMatches.get_za3lpa$(index_0 - 1 | 0).recognizer.outputPattern : '') + s;
    }
    var pattern = accumulator;
    return new RecognizerCombiner$RegularExpression('^' + pattern + '$');
  };
  RecognizerCombiner$Companion.prototype.escapeRegex_0 = function (input) {
    return Regex_init('([.\\\\^$\\[{}()*?+])').replace_x2uqeu$(input, '\\$1');
  };
  RecognizerCombiner$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var RecognizerCombiner$Companion_instance = null;
  function RecognizerCombiner$Companion_getInstance() {
    if (RecognizerCombiner$Companion_instance === null) {
      new RecognizerCombiner$Companion();
    }
    return RecognizerCombiner$Companion_instance;
  }
  function RecognizerCombiner$Options(onlyPatterns, caseSensitive, dotMatchesLineBreaks, multiline) {
    if (onlyPatterns === void 0)
      onlyPatterns = false;
    if (caseSensitive === void 0)
      caseSensitive = true;
    if (dotMatchesLineBreaks === void 0)
      dotMatchesLineBreaks = false;
    if (multiline === void 0)
      multiline = false;
    this.onlyPatterns = onlyPatterns;
    this.caseSensitive = caseSensitive;
    this.dotMatchesLineBreaks = dotMatchesLineBreaks;
    this.multiline = multiline;
  }
  RecognizerCombiner$Options.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Options',
    interfaces: []
  };
  RecognizerCombiner$Options.prototype.component1 = function () {
    return this.onlyPatterns;
  };
  RecognizerCombiner$Options.prototype.component2 = function () {
    return this.caseSensitive;
  };
  RecognizerCombiner$Options.prototype.component3 = function () {
    return this.dotMatchesLineBreaks;
  };
  RecognizerCombiner$Options.prototype.component4 = function () {
    return this.multiline;
  };
  RecognizerCombiner$Options.prototype.copy_nyyhg$ = function (onlyPatterns, caseSensitive, dotMatchesLineBreaks, multiline) {
    return new RecognizerCombiner$Options(onlyPatterns === void 0 ? this.onlyPatterns : onlyPatterns, caseSensitive === void 0 ? this.caseSensitive : caseSensitive, dotMatchesLineBreaks === void 0 ? this.dotMatchesLineBreaks : dotMatchesLineBreaks, multiline === void 0 ? this.multiline : multiline);
  };
  RecognizerCombiner$Options.prototype.toString = function () {
    return 'Options(onlyPatterns=' + Kotlin.toString(this.onlyPatterns) + (', caseSensitive=' + Kotlin.toString(this.caseSensitive)) + (', dotMatchesLineBreaks=' + Kotlin.toString(this.dotMatchesLineBreaks)) + (', multiline=' + Kotlin.toString(this.multiline)) + ')';
  };
  RecognizerCombiner$Options.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.onlyPatterns) | 0;
    result = result * 31 + Kotlin.hashCode(this.caseSensitive) | 0;
    result = result * 31 + Kotlin.hashCode(this.dotMatchesLineBreaks) | 0;
    result = result * 31 + Kotlin.hashCode(this.multiline) | 0;
    return result;
  };
  RecognizerCombiner$Options.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.onlyPatterns, other.onlyPatterns) && Kotlin.equals(this.caseSensitive, other.caseSensitive) && Kotlin.equals(this.dotMatchesLineBreaks, other.dotMatchesLineBreaks) && Kotlin.equals(this.multiline, other.multiline)))));
  };
  function RecognizerCombiner$RegularExpression(pattern) {
    this.pattern = pattern;
    this.regex_8h9uer$_0 = lazy(RecognizerCombiner$RegularExpression$regex$lambda(this));
  }
  Object.defineProperty(RecognizerCombiner$RegularExpression.prototype, 'regex', {
    get: function () {
      return this.regex_8h9uer$_0.value;
    }
  });
  function RecognizerCombiner$RegularExpression$regex$lambda(this$RegularExpression) {
    return function () {
      return Regex_init(this$RegularExpression.pattern);
    };
  }
  RecognizerCombiner$RegularExpression.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'RegularExpression',
    interfaces: []
  };
  RecognizerCombiner$RegularExpression.prototype.component1 = function () {
    return this.pattern;
  };
  RecognizerCombiner$RegularExpression.prototype.copy_61zpoe$ = function (pattern) {
    return new RecognizerCombiner$RegularExpression(pattern === void 0 ? this.pattern : pattern);
  };
  RecognizerCombiner$RegularExpression.prototype.toString = function () {
    return 'RegularExpression(pattern=' + Kotlin.toString(this.pattern) + ')';
  };
  RecognizerCombiner$RegularExpression.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.pattern) | 0;
    return result;
  };
  RecognizerCombiner$RegularExpression.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && Kotlin.equals(this.pattern, other.pattern))));
  };
  RecognizerCombiner.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'RecognizerCombiner',
    interfaces: []
  };
  function SimpleRecognizer(name, outputPattern, description, active, searchPattern, mainGroupIndex, mainGroupName) {
    if (description === void 0)
      description = null;
    if (active === void 0)
      active = true;
    if (searchPattern === void 0)
      searchPattern = null;
    if (mainGroupIndex === void 0)
      mainGroupIndex = 1;
    if (mainGroupName === void 0)
      mainGroupName = null;
    this.name_dn1z7s$_0 = name;
    this.outputPattern_qgiqgi$_0 = outputPattern;
    this.description_l8a38b$_0 = description;
    this.active_8xsxxv$_0 = active;
    this.searchPattern_0 = searchPattern;
    this.mainGroupIndex_0 = mainGroupIndex;
    this.mainGroupName_0 = mainGroupName;
    this.searchRegex_btrjsy$_0 = lazy(SimpleRecognizer$searchRegex$lambda(this));
    if (this.mainGroupIndex_0 == null && this.mainGroupName_0 == null) {
      throw IllegalArgumentException_init('Either mainGroupIndex or mainGroupName must be defined.');
    }
  }
  Object.defineProperty(SimpleRecognizer.prototype, 'name', {
    get: function () {
      return this.name_dn1z7s$_0;
    }
  });
  Object.defineProperty(SimpleRecognizer.prototype, 'outputPattern', {
    get: function () {
      return this.outputPattern_qgiqgi$_0;
    }
  });
  Object.defineProperty(SimpleRecognizer.prototype, 'description', {
    get: function () {
      return this.description_l8a38b$_0;
    }
  });
  Object.defineProperty(SimpleRecognizer.prototype, 'active', {
    get: function () {
      return this.active_8xsxxv$_0;
    }
  });
  Object.defineProperty(SimpleRecognizer.prototype, 'searchRegex_0', {
    get: function () {
      return this.searchRegex_btrjsy$_0.value;
    }
  });
  function SimpleRecognizer$findMatches$lambda(this$SimpleRecognizer) {
    return function (result) {
      return new RecognizerMatch(void 0, this$SimpleRecognizer.getMainGroupRange_0(result), void 0, this$SimpleRecognizer.getMainGroupValue_0(result), this$SimpleRecognizer);
    };
  }
  SimpleRecognizer.prototype.findMatches_61zpoe$ = function (input) {
    return toList(map(this.searchRegex_0.findAll_905azu$(input), SimpleRecognizer$findMatches$lambda(this)));
  };
  SimpleRecognizer.prototype.getMainGroupValue_0 = function (result) {
    var tmp$, tmp$_0, tmp$_1, tmp$_2, tmp$_3;
    if (this.mainGroupName_0 != null) {
      tmp$_1 = (tmp$_0 = (Kotlin.isType(tmp$ = result.groups, MatchNamedGroupCollection) ? tmp$ : throwCCE()).get_61zpoe$(this.mainGroupName_0)) != null ? tmp$_0.value : null;
      if (tmp$_1 == null) {
        throw Exception_init("Unable to find group '" + toString(this.mainGroupName_0) + "'");
      }
      return tmp$_1;
    }
     else if (this.mainGroupIndex_0 != null) {
      tmp$_3 = (tmp$_2 = result.groups.get_za3lpa$(this.mainGroupIndex_0)) != null ? tmp$_2.value : null;
      if (tmp$_3 == null) {
        throw Exception_init('Unable to find group with index ' + toString(this.mainGroupIndex_0) + '.');
      }
      return tmp$_3;
    }
     else
      throw RuntimeException_init('Neither mainGroupName nor mainGroupIndex defined.');
  };
  SimpleRecognizer.prototype.getMainGroupRange_0 = function (result) {
    var mainGroupValue = this.getMainGroupValue_0(result);
    var start = indexOf(result.value, mainGroupValue);
    return new IntRange(result.range.first + start | 0, result.range.first + start + mainGroupValue.length - 1 | 0);
  };
  function SimpleRecognizer$searchRegex$lambda(this$SimpleRecognizer) {
    return function () {
      var tmp$, tmp$_0;
      return Regex_init((tmp$_0 = (tmp$ = this$SimpleRecognizer.searchPattern_0) != null ? replace(tmp$, '%s', this$SimpleRecognizer.outputPattern) : null) != null ? tmp$_0 : '(' + this$SimpleRecognizer.outputPattern + ')');
    };
  }
  SimpleRecognizer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SimpleRecognizer',
    interfaces: [Recognizer]
  };
  SimpleRecognizer.prototype.component1 = function () {
    return this.name;
  };
  SimpleRecognizer.prototype.component2 = function () {
    return this.outputPattern;
  };
  SimpleRecognizer.prototype.component3 = function () {
    return this.description;
  };
  SimpleRecognizer.prototype.component4 = function () {
    return this.active;
  };
  SimpleRecognizer.prototype.component5_0 = function () {
    return this.searchPattern_0;
  };
  SimpleRecognizer.prototype.component6_0 = function () {
    return this.mainGroupIndex_0;
  };
  SimpleRecognizer.prototype.component7_0 = function () {
    return this.mainGroupName_0;
  };
  SimpleRecognizer.prototype.copy_f7157r$ = function (name, outputPattern, description, active, searchPattern, mainGroupIndex, mainGroupName) {
    return new SimpleRecognizer(name === void 0 ? this.name : name, outputPattern === void 0 ? this.outputPattern : outputPattern, description === void 0 ? this.description : description, active === void 0 ? this.active : active, searchPattern === void 0 ? this.searchPattern_0 : searchPattern, mainGroupIndex === void 0 ? this.mainGroupIndex_0 : mainGroupIndex, mainGroupName === void 0 ? this.mainGroupName_0 : mainGroupName);
  };
  SimpleRecognizer.prototype.toString = function () {
    return 'SimpleRecognizer(name=' + Kotlin.toString(this.name) + (', outputPattern=' + Kotlin.toString(this.outputPattern)) + (', description=' + Kotlin.toString(this.description)) + (', active=' + Kotlin.toString(this.active)) + (', searchPattern=' + Kotlin.toString(this.searchPattern_0)) + (', mainGroupIndex=' + Kotlin.toString(this.mainGroupIndex_0)) + (', mainGroupName=' + Kotlin.toString(this.mainGroupName_0)) + ')';
  };
  SimpleRecognizer.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.name) | 0;
    result = result * 31 + Kotlin.hashCode(this.outputPattern) | 0;
    result = result * 31 + Kotlin.hashCode(this.description) | 0;
    result = result * 31 + Kotlin.hashCode(this.active) | 0;
    result = result * 31 + Kotlin.hashCode(this.searchPattern_0) | 0;
    result = result * 31 + Kotlin.hashCode(this.mainGroupIndex_0) | 0;
    result = result * 31 + Kotlin.hashCode(this.mainGroupName_0) | 0;
    return result;
  };
  SimpleRecognizer.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.name, other.name) && Kotlin.equals(this.outputPattern, other.outputPattern) && Kotlin.equals(this.description, other.description) && Kotlin.equals(this.active, other.active) && Kotlin.equals(this.searchPattern_0, other.searchPattern_0) && Kotlin.equals(this.mainGroupIndex_0, other.mainGroupIndex_0) && Kotlin.equals(this.mainGroupName_0, other.mainGroupName_0)))));
  };
  var KEY_LAST_VERSION;
  var KEY_LAST_VISIT;
  var VAL_VERSION;
  var VAL_EXAMPLE_INPUT;
  function main() {
    initRegexGenerator();
  }
  function initRegexGenerator() {
    try {
      var presenter = new SimplePresenter();
      var input = isBlank(presenter.currentTextInput) ? VAL_EXAMPLE_INPUT : presenter.currentTextInput;
      presenter.recognizeMatches_61zpoe$(input);
      var showGuide = isNewUser();
      storeUserLastInfo();
      if (showGuide) {
        presenter.showInitialUserGuide();
      }
    }
     catch (exception) {
      if (Kotlin.isType(exception, Exception)) {
        console.error(exception);
        window.alert('Unable to initialize RegexGenerator: ' + toString(exception.message));
      }
       else
        throw exception;
    }
  }
  function isNewUser() {
    var tmp$, tmp$_0, tmp$_1, tmp$_2;
    return ((tmp$_0 = (tmp$ = localStorage.getItem(KEY_LAST_VISIT)) != null ? toBoolean(tmp$) : null) != null ? tmp$_0 : true) || ((tmp$_2 = (tmp$_1 = localStorage.getItem(KEY_LAST_VERSION)) != null ? toIntOrNull(tmp$_1) : null) != null ? tmp$_2 : 3) < 3;
  }
  function storeUserLastInfo() {
    localStorage[KEY_LAST_VISIT] = (new Date()).toISOString();
    localStorage[KEY_LAST_VERSION] = (3).toString();
  }
  function DisplayContract() {
  }
  function DisplayContract$View() {
  }
  DisplayContract$View.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'View',
    interfaces: []
  };
  function DisplayContract$Presenter() {
  }
  DisplayContract$Presenter.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Presenter',
    interfaces: []
  };
  DisplayContract.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'DisplayContract',
    interfaces: []
  };
  var ELEMENT_DIV;
  var ELEMENT_BUTTON;
  var ELEMENT_PRE;
  var ELEMENT_CODE;
  function HtmlHelper() {
    HtmlHelper_instance = this;
  }
  HtmlHelper.prototype.getDivById_y4putb$ = function (id) {
    var tmp$;
    try {
      return Kotlin.isType(tmp$ = document.getElementById(id), HTMLDivElement) ? tmp$ : throwCCE();
    }
     catch (e) {
      if (Kotlin.isType(e, ClassCastException)) {
        throw new RuntimeException("Unable to find div with id '" + id + "'.", e);
      }
       else
        throw e;
    }
  };
  HtmlHelper.prototype.getInputById_y4putb$ = function (id) {
    var tmp$;
    try {
      return Kotlin.isType(tmp$ = document.getElementById(id), HTMLInputElement) ? tmp$ : throwCCE();
    }
     catch (e) {
      if (Kotlin.isType(e, ClassCastException)) {
        throw new RuntimeException("Unable to find input with id '" + id + "'.", e);
      }
       else
        throw e;
    }
  };
  HtmlHelper.prototype.getButtonById_y4putb$ = function (id) {
    var tmp$;
    try {
      return Kotlin.isType(tmp$ = document.getElementById(id), HTMLButtonElement) ? tmp$ : throwCCE();
    }
     catch (e) {
      if (Kotlin.isType(e, ClassCastException)) {
        throw new RuntimeException("Unable to find button with id '" + id + "'.", e);
      }
       else
        throw e;
    }
  };
  HtmlHelper.prototype.getLinkById_y4putb$ = function (id) {
    var tmp$;
    try {
      return Kotlin.isType(tmp$ = document.getElementById(id), HTMLAnchorElement) ? tmp$ : throwCCE();
    }
     catch (e) {
      if (Kotlin.isType(e, ClassCastException)) {
        throw new RuntimeException("Unable to find link with id '" + id + "'.", e);
      }
       else
        throw e;
    }
  };
  HtmlHelper.prototype.createDivElement_9utdl7$ = function (parent, classNames) {
    var tmp$;
    return Kotlin.isType(tmp$ = this.createElement_0(parent, ELEMENT_DIV, classNames.slice()), HTMLDivElement) ? tmp$ : throwCCE();
  };
  HtmlHelper.prototype.createButtonElement_9utdl7$ = function (parent, classNames) {
    var tmp$;
    return Kotlin.isType(tmp$ = this.createElement_0(parent, ELEMENT_BUTTON, classNames.slice()), HTMLButtonElement) ? tmp$ : throwCCE();
  };
  HtmlHelper.prototype.createPreElement_9utdl7$ = function (parent, classNames) {
    var tmp$;
    return Kotlin.isType(tmp$ = this.createElement_0(parent, ELEMENT_PRE, classNames.slice()), HTMLPreElement) ? tmp$ : throwCCE();
  };
  HtmlHelper.prototype.createCodeElement_9utdl7$ = function (parent, classNames) {
    var tmp$;
    return Kotlin.isType(tmp$ = this.createElement_0(parent, ELEMENT_CODE, classNames.slice()), HTMLElement) ? tmp$ : throwCCE();
  };
  HtmlHelper.prototype.createElement_0 = function (parent, type, classNames) {
    var element = document.createElement(type);
    addClass(element, classNames.slice());
    parent.appendChild(element);
    return element;
  };
  HtmlHelper.prototype.toggleClass_g7pq4p$ = function (element, selected, className) {
    if (selected)
      addClass(element, [className]);
    else
      removeClass(element, [className]);
  };
  HtmlHelper.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'HtmlHelper',
    interfaces: []
  };
  var HtmlHelper_instance = null;
  function HtmlHelper_getInstance() {
    if (HtmlHelper_instance === null) {
      new HtmlHelper();
    }
    return HtmlHelper_instance;
  }
  var CLASS_MATCH_ROW;
  var CLASS_MATCH_ITEM;
  var CLASS_ITEM_SELECTED;
  var CLASS_ITEM_NOT_AVAILABLE;
  var EVENT_CLICK;
  var EVENT_INPUT;
  var ID_INPUT_ELEMENT;
  var ID_TEXT_DISPLAY;
  var ID_RESULT_DISPLAY;
  var ID_ROW_CONTAINER;
  var ID_CONTAINER_INPUT;
  var ID_CHECK_ONLY_MATCHES;
  var ID_CHECK_CASE_INSENSITIVE;
  var ID_CHECK_DOT_MATCHES_LINE_BRAKES;
  var ID_CHECK_MULTILINE;
  var ID_BUTTON_COPY;
  var ID_BUTTON_HELP;
  var ID_DIV_LANGUAGES;
  function get_characterUnits($receiver) {
    return $receiver.toString() + 'ch';
  }
  function HtmlPage(presenter) {
    this.presenter_0 = presenter;
    this.textInput_0 = HtmlHelper_getInstance().getInputById_y4putb$(ID_INPUT_ELEMENT);
    this.textDisplay_0 = HtmlHelper_getInstance().getDivById_y4putb$(ID_TEXT_DISPLAY);
    this.rowContainer_0 = HtmlHelper_getInstance().getDivById_y4putb$(ID_ROW_CONTAINER);
    this.resultDisplay_0 = HtmlHelper_getInstance().getDivById_y4putb$(ID_RESULT_DISPLAY);
    this.buttonCopy_0 = HtmlHelper_getInstance().getButtonById_y4putb$(ID_BUTTON_COPY);
    this.buttonHelp_0 = HtmlHelper_getInstance().getLinkById_y4putb$(ID_BUTTON_HELP);
    this.checkOnlyMatches_0 = HtmlHelper_getInstance().getInputById_y4putb$(ID_CHECK_ONLY_MATCHES);
    this.checkCaseInsensitive_0 = HtmlHelper_getInstance().getInputById_y4putb$(ID_CHECK_CASE_INSENSITIVE);
    this.checkDotAll_0 = HtmlHelper_getInstance().getInputById_y4putb$(ID_CHECK_DOT_MATCHES_LINE_BRAKES);
    this.checkMultiline_0 = HtmlHelper_getInstance().getInputById_y4putb$(ID_CHECK_MULTILINE);
    this.containerLanguages_0 = HtmlHelper_getInstance().getDivById_y4putb$(ID_DIV_LANGUAGES);
    this.recognizerMatchToRow_0 = LinkedHashMap_init();
    this.recognizerMatchToElements_0 = LinkedHashMap_init();
    var $receiver = CodeGenerator$Companion_getInstance().list;
    var destination = ArrayList_init_0(collectionSizeOrDefault($receiver, 10));
    var tmp$;
    tmp$ = $receiver.iterator();
    while (tmp$.hasNext()) {
      var item = tmp$.next();
      destination.add_11rb$(to(item, new LanguageCard(item, this.containerLanguages_0)));
    }
    this.languageDisplays_0 = toMap(destination);
    this.driver_0 = new Driver({});
    this.textInput_0.addEventListener(EVENT_INPUT, HtmlPage_init$lambda(this));
    this.buttonCopy_0.addEventListener(EVENT_CLICK, HtmlPage_init$lambda_0(this));
    this.buttonHelp_0.addEventListener(EVENT_CLICK, HtmlPage_init$lambda_1(this));
    this.checkCaseInsensitive_0.addEventListener(EVENT_INPUT, HtmlPage_init$lambda_2(this));
    this.checkDotAll_0.addEventListener(EVENT_INPUT, HtmlPage_init$lambda_3(this));
    this.checkMultiline_0.addEventListener(EVENT_INPUT, HtmlPage_init$lambda_4(this));
    this.checkOnlyMatches_0.addEventListener(EVENT_INPUT, HtmlPage_init$lambda_5(this));
  }
  HtmlPage.prototype.get_row_0 = function ($receiver) {
    return this.recognizerMatchToRow_0.get_11rb$($receiver);
  };
  HtmlPage.prototype.get_div_0 = function ($receiver) {
    return this.recognizerMatchToElements_0.get_11rb$($receiver);
  };
  HtmlPage.prototype.showCopyButton_6taknv$ = function (visible) {
    var button = jQuery(this.buttonCopy_0);
    if (visible) {
      button.show();
    }
     else {
      button.hide();
    }
  };
  HtmlPage.prototype.selectInputText = function () {
    this.textInput_0.select();
  };
  Object.defineProperty(HtmlPage.prototype, 'inputText', {
    get: function () {
      return this.textInput_0.value;
    },
    set: function (value) {
      this.textInput_0.value = value;
    }
  });
  Object.defineProperty(HtmlPage.prototype, 'displayText', {
    get: function () {
      return this.textDisplay_0.innerText;
    },
    set: function (value) {
      this.textDisplay_0.innerText = value;
    }
  });
  Object.defineProperty(HtmlPage.prototype, 'resultText', {
    get: function () {
      return this.resultDisplay_0.innerText;
    },
    set: function (value) {
      this.resultDisplay_0.innerText = value;
    }
  });
  function HtmlPage$showResults$getCssClass(closure$classes, closure$index) {
    return function () {
      var tmp$;
      return 'bg-' + closure$classes.get_za3lpa$((tmp$ = closure$index.v, closure$index.v = tmp$ + 1 | 0, tmp$) % closure$classes.size);
    };
  }
  function HtmlPage$showResults$getElementTitle(match) {
    return match.recognizer.name + ' (' + match.inputPart + ')';
  }
  function HtmlPage$showResults$lambda$lambda(this$HtmlPage, closure$match) {
    return function (it) {
      this$HtmlPage.presenter_0.onSuggestionClick_hdji9c$(closure$match);
      return Unit;
    };
  }
  HtmlPage.prototype.showResults_oz4rjz$ = function (matches) {
    var tmp$;
    var index = {v: 0};
    var classes = listOf(['primary', 'success', 'danger', 'warning']);
    var getCssClass = HtmlPage$showResults$getCssClass(classes, index);
    var getElementTitle = HtmlPage$showResults$getElementTitle;
    clear(this.rowContainer_0);
    this.recognizerMatchToRow_0.clear();
    this.recognizerMatchToElements_0.clear();
    this.recognizerMatchToRow_0.putAll_a2k3zr$(this.distributeToRows_0(matches));
    var $receiver = new IntRange(0, (tmp$ = max(this.recognizerMatchToRow_0.values)) != null ? tmp$ : 0);
    var destination = ArrayList_init_0(collectionSizeOrDefault($receiver, 10));
    var tmp$_0;
    tmp$_0 = $receiver.iterator();
    while (tmp$_0.hasNext()) {
      var item = tmp$_0.next();
      destination.add_11rb$(this.createRowElement_0());
    }
    var rowElements = toList_0(destination);
    var tmp$_1;
    tmp$_1 = matches.iterator();
    while (tmp$_1.hasNext()) {
      var element = tmp$_1.next();
      var rowElement = rowElements.get_za3lpa$(ensureNotNull(this.get_row_0(element)));
      var element_0 = this.createMatchElement_0(rowElement);
      this.recognizerMatchToElements_0.put_xwzc9p$(element, element_0);
      addClass(element_0, [getCssClass()]);
      element_0.style.width = get_characterUnits(element.inputPart.length);
      element_0.style.left = get_characterUnits(element.first);
      element_0.title = getElementTitle(element);
      element_0.addEventListener(EVENT_CLICK, HtmlPage$showResults$lambda$lambda(this, element));
    }
  };
  function HtmlPage$distributeToRows$createNextLine(closure$lines) {
    return function () {
      closure$lines.add_11rb$(0);
      return closure$lines.size - 1 | 0;
    };
  }
  function HtmlPage$distributeToRows$lambda(it) {
    return it.first;
  }
  function HtmlPage$distributeToRows$lambda_0(it) {
    return -it.length | 0;
  }
  HtmlPage.prototype.distributeToRows_0 = function (matches) {
    var lines = ArrayList_init();
    var createNextLine = HtmlPage$distributeToRows$createNextLine(lines);
    var $receiver = sortedWith(matches, compareBy([HtmlPage$distributeToRows$lambda, HtmlPage$distributeToRows$lambda_0]));
    var destination = ArrayList_init_0(collectionSizeOrDefault($receiver, 10));
    var tmp$;
    tmp$ = $receiver.iterator();
    loop_label: while (tmp$.hasNext()) {
      var item = tmp$.next();
      var tmp$_0 = destination.add_11rb$;
      var indexOfFirst$result;
      indexOfFirst$break: do {
        var tmp$_1;
        var index = 0;
        tmp$_1 = lines.iterator();
        while (tmp$_1.hasNext()) {
          var item_0 = tmp$_1.next();
          if (item_0 < item.first) {
            indexOfFirst$result = index;
            break indexOfFirst$break;
          }
          index = index + 1 | 0;
        }
        indexOfFirst$result = -1;
      }
       while (false);
      var indexOfFreeLine = indexOfFirst$result;
      var line = indexOfFreeLine >= 0 ? indexOfFreeLine : createNextLine();
      lines.set_wxm5ur$(line, item.last);
      tmp$_0.call(destination, to(item, line));
    }
    return toMap(destination);
  };
  HtmlPage.prototype.createRowElement_0 = function () {
    return HtmlHelper_getInstance().createDivElement_9utdl7$(this.rowContainer_0, [CLASS_MATCH_ROW]);
  };
  HtmlPage.prototype.createMatchElement_0 = function (parent) {
    return HtmlHelper_getInstance().createDivElement_9utdl7$(parent, [CLASS_MATCH_ITEM]);
  };
  HtmlPage.prototype.select_n54ctl$ = function (match, selected) {
    var tmp$;
    if ((tmp$ = this.get_div_0(match)) != null) {
      HtmlHelper_getInstance().toggleClass_g7pq4p$(tmp$, selected, CLASS_ITEM_SELECTED);
    }
  };
  HtmlPage.prototype.disable_n54ctl$ = function (match, disabled) {
    var tmp$;
    if ((tmp$ = this.get_div_0(match)) != null) {
      HtmlHelper_getInstance().toggleClass_g7pq4p$(tmp$, disabled, CLASS_ITEM_NOT_AVAILABLE);
    }
  };
  Object.defineProperty(HtmlPage.prototype, 'options', {
    get: function () {
      return new RecognizerCombiner$Options(this.checkOnlyMatches_0.checked, this.checkCaseInsensitive_0.checked, this.checkDotAll_0.checked, this.checkMultiline_0.checked);
    }
  });
  HtmlPage.prototype.showGeneratedCodeForPattern_61zpoe$ = function (pattern) {
    var options = this.options;
    var tmp$;
    tmp$ = CodeGenerator$Companion_getInstance().list.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      var tmp$_0;
      (tmp$_0 = this.languageDisplays_0.get_11rb$(element)) != null ? (tmp$_0.setSnippet_eos4sh$(element.generateCode_wa467u$(pattern, options)), Unit) : null;
    }
    Prism.highlightAll();
  };
  HtmlPage.prototype.showUserGuide_6taknv$ = function (initialStep) {
    this.driver_0.reset();
    var steps = [createStepDefinition('#rg-title', 'New to Regex Generator', "Hi! It looks like you're new to <em>Regex Generator<\/em>. Let us show you how to use this tool.", 'right'), createStepDefinition('#rg_input_container', 'Sample', 'In the first step we need an example, so please write or paste an example of the text you want to recognize with your regex.', 'bottom-center'), createStepDefinition('#rg_result_container', 'Recognition', 'Regex Generator will immediately analyze your text and suggest common patterns you may use.', 'top-center'), createStepDefinition('#rg_row_container', 'Suggestions', 'Click one or more of suggested patterns...', 'top'), createStepDefinition('#rg_result_display_box', 'Result', '... and we will generate a first <em>regular expression<\/em> for you. It should be able to match your input text.', 'top-center'), createStepDefinition('#rg_language_accordion', 'Language snippets', 'We will also generate snippets for some languages that show you, how to use the regular expression in your favourite language.', 'top-left')];
    this.driver_0.defineSteps(steps);
    this.driver_0.start(initialStep ? 0 : 1);
  };
  function HtmlPage_init$lambda(this$HtmlPage) {
    return function (it) {
      this$HtmlPage.presenter_0.onInputChanges_61zpoe$(this$HtmlPage.inputText);
      return Unit;
    };
  }
  function HtmlPage_init$lambda_0(this$HtmlPage) {
    return function (it) {
      this$HtmlPage.presenter_0.onButtonCopyClick();
      return Unit;
    };
  }
  function HtmlPage_init$lambda_1(this$HtmlPage) {
    return function (it) {
      this$HtmlPage.presenter_0.onButtonHelpClick();
      return Unit;
    };
  }
  function HtmlPage_init$lambda_2(this$HtmlPage) {
    return function (it) {
      this$HtmlPage.presenter_0.onOptionsChange_ow7xd4$(this$HtmlPage.options);
      return Unit;
    };
  }
  function HtmlPage_init$lambda_3(this$HtmlPage) {
    return function (it) {
      this$HtmlPage.presenter_0.onOptionsChange_ow7xd4$(this$HtmlPage.options);
      return Unit;
    };
  }
  function HtmlPage_init$lambda_4(this$HtmlPage) {
    return function (it) {
      this$HtmlPage.presenter_0.onOptionsChange_ow7xd4$(this$HtmlPage.options);
      return Unit;
    };
  }
  function HtmlPage_init$lambda_5(this$HtmlPage) {
    return function (it) {
      this$HtmlPage.presenter_0.onOptionsChange_ow7xd4$(this$HtmlPage.options);
      return Unit;
    };
  }
  HtmlPage.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'HtmlPage',
    interfaces: [DisplayContract$View]
  };
  function visitAndFinalize$lambda(closure$block) {
    return function ($receiver) {
      closure$block($receiver);
      return Unit;
    };
  }
  function div$lambda($receiver) {
    return Unit;
  }
  function visitAndFinalize$lambda_0(closure$block) {
    return function ($receiver) {
      closure$block($receiver);
      return Unit;
    };
  }
  function p$lambda($receiver) {
    return Unit;
  }
  function visit$lambda(closure$block) {
    return function ($receiver) {
      closure$block($receiver);
      return Unit;
    };
  }
  function button$lambda($receiver) {
    return Unit;
  }
  function visit$lambda_0(closure$block) {
    return function ($receiver) {
      closure$block($receiver);
      return Unit;
    };
  }
  function code$lambda($receiver) {
    return Unit;
  }
  function visit$lambda_1(closure$block) {
    return function ($receiver) {
      closure$block($receiver);
      return Unit;
    };
  }
  function pre$lambda($receiver) {
    return Unit;
  }
  function visit$lambda_2(closure$block) {
    return function ($receiver) {
      closure$block($receiver);
      return Unit;
    };
  }
  function div$lambda_0($receiver) {
    return Unit;
  }
  function LanguageCard(codeGenerator, parent) {
    this.codeGenerator_0 = codeGenerator;
    this.bodyElement_0 = null;
    this.codeElement_0 = null;
    this.warnings_0 = ArrayList_init();
    var tmp$, tmp$_0;
    var $receiver = get_create(document);
    var tmp$_1;
    parent.appendChild(Kotlin.isType(tmp$_1 = visitTagAndFinalize(new DIV_init(attributesMapOf('class', 'card'), $receiver), $receiver, visitAndFinalize$lambda(LanguageCard_init$lambda(this))), HTMLDivElement_0) ? tmp$_1 : throwCCE());
    this.codeElement_0 = Kotlin.isType(tmp$ = document.getElementById(this.codeElementId_0), HTMLElement) ? tmp$ : throwCCE();
    this.bodyElement_0 = Kotlin.isType(tmp$_0 = document.getElementById(this.bodyElementId_0), HTMLElement) ? tmp$_0 : throwCCE();
    jQuery(this.bodyElement_0).on('shown.bs.collapse', LanguageCard_init$lambda_0(this));
    jQuery(this.bodyElement_0).on('hidden.bs.collapse', LanguageCard_init$lambda_1(this));
  }
  Object.defineProperty(LanguageCard.prototype, 'bodyElementId_0', {
    get: function () {
      return this.codeGenerator_0.uniqueName + '_body';
    }
  });
  Object.defineProperty(LanguageCard.prototype, 'codeElementId_0', {
    get: function () {
      return this.codeGenerator_0.uniqueName + '_code';
    }
  });
  Object.defineProperty(LanguageCard.prototype, 'shownPropertyName_0', {
    get: function () {
      return 'language.' + this.codeGenerator_0.uniqueName + '.expanded';
    }
  });
  Object.defineProperty(LanguageCard.prototype, 'shown_0', {
    get: function () {
      var tmp$, tmp$_0;
      return (tmp$_0 = (tmp$ = localStorage.getItem(this.shownPropertyName_0)) != null ? toBoolean(tmp$) : null) != null ? tmp$_0 : false;
    },
    set: function (value) {
      localStorage.setItem(this.shownPropertyName_0, value.toString());
    }
  });
  LanguageCard.prototype.setSnippet_eos4sh$ = function (snippet) {
    this.code_0 = snippet.snippet;
    var tmp$;
    tmp$ = this.warnings_0.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      var tmp$_0;
      (tmp$_0 = element.parentElement) != null ? tmp$_0.removeChild(element) : null;
    }
    this.warnings_0.clear();
    var tmp$_1;
    tmp$_1 = snippet.warnings.iterator();
    while (tmp$_1.hasNext()) {
      var element_0 = tmp$_1.next();
      var warningElement = this.createWarning_0(element_0);
      this.warnings_0.add_11rb$(warningElement);
      this.bodyElement_0.prepend(warningElement);
    }
  };
  function LanguageCard$createWarning$lambda(closure$text) {
    return function ($receiver) {
      $receiver.unaryPlus_pdl1vz$(closure$text);
      return Unit;
    };
  }
  LanguageCard.prototype.createWarning_0 = function (text) {
    var $receiver = get_create(document);
    return visitTagAndFinalize(new P_init(attributesMapOf('class', 'alert alert-warning rounded m-2'), $receiver), $receiver, visitAndFinalize$lambda_0(LanguageCard$createWarning$lambda(text)));
  };
  Object.defineProperty(LanguageCard.prototype, 'code_0', {
    get: function () {
      return this.codeElement_0.innerHTML;
    },
    set: function (value) {
      this.codeElement_0.innerHTML = this.escapeHTML_0(value);
    }
  });
  LanguageCard.prototype.escapeHTML_0 = function ($receiver) {
    var text = $receiver;
    if (text.length === 0)
      return text;
    var $receiver_0 = StringBuilder_init($receiver.length);
    var tmp$;
    tmp$ = iterator(text);
    while (tmp$.hasNext()) {
      var element = unboxChar(tmp$.next());
      var ch = element;
      switch (ch) {
        case 39:
          $receiver_0.append_gw00v9$('&apos;');
          break;
        case 34:
          $receiver_0.append_gw00v9$('&quot');
          break;
        case 38:
          $receiver_0.append_gw00v9$('&amp;');
          break;
        case 60:
          $receiver_0.append_gw00v9$('&lt;');
          break;
        case 62:
          $receiver_0.append_gw00v9$('&gt;');
          break;
        default:$receiver_0.append_s8itvh$(ch);
          break;
      }
    }
    return $receiver_0.toString();
  };
  function LanguageCard_init$lambda$lambda$lambda(this$LanguageCard) {
    return function ($receiver) {
      var $receiver_0 = $receiver.attributes;
      var key = 'data-toggle';
      var value = 'collapse';
      $receiver_0.put_xwzc9p$(key, value);
      var $receiver_1 = $receiver.attributes;
      var key_0 = 'data-target';
      var value_0 = '#' + this$LanguageCard.bodyElementId_0;
      $receiver_1.put_xwzc9p$(key_0, value_0);
      $receiver.unaryPlus_pdl1vz$(this$LanguageCard.codeGenerator_0.languageName);
      return Unit;
    };
  }
  function LanguageCard_init$lambda$lambda(this$LanguageCard) {
    return function ($receiver) {
      var tmp$, tmp$_0;
      tmp$ = ButtonType.button;
      tmp$_0 = LanguageCard_init$lambda$lambda$lambda(this$LanguageCard);
      visitTag(new BUTTON_init(attributesMapOf_0(['formenctype', null != null ? enumEncode(null) : null, 'formmethod', null != null ? enumEncode(null) : null, 'name', null, 'type', tmp$ != null ? enumEncode(tmp$) : null, 'class', 'btn btn-link']), $receiver.consumer), visit$lambda(tmp$_0));
      return Unit;
    };
  }
  function LanguageCard_init$lambda$lambda$lambda$lambda(this$LanguageCard) {
    return function ($receiver) {
      set_id($receiver, this$LanguageCard.codeElementId_0);
      return Unit;
    };
  }
  function LanguageCard_init$lambda$lambda$lambda_0(this$LanguageCard) {
    return function ($receiver) {
      var classes = 'language-' + this$LanguageCard.codeGenerator_0.highlightLanguage;
      var block = LanguageCard_init$lambda$lambda$lambda$lambda(this$LanguageCard);
      visitTag(new CODE_init(attributesMapOf('class', classes), $receiver.consumer), visit$lambda_0(block));
      return Unit;
    };
  }
  function LanguageCard_init$lambda$lambda_0(this$LanguageCard) {
    return function ($receiver) {
      set_id($receiver, this$LanguageCard.bodyElementId_0);
      var block = LanguageCard_init$lambda$lambda$lambda_0(this$LanguageCard);
      visitTag(new PRE_init(attributesMapOf('class', null), $receiver.consumer), visit$lambda_1(block));
      return Unit;
    };
  }
  function LanguageCard_init$lambda(this$LanguageCard) {
    return function ($receiver) {
      var classes = 'card-header';
      var block = LanguageCard_init$lambda$lambda(this$LanguageCard);
      visitTag(new DIV_init(attributesMapOf('class', classes), $receiver.consumer), visit$lambda_2(block));
      var classes_0 = 'collapse ' + (this$LanguageCard.shown_0 ? 'show' : '');
      var block_0 = LanguageCard_init$lambda$lambda_0(this$LanguageCard);
      visitTag(new DIV_init(attributesMapOf('class', classes_0), $receiver.consumer), visit$lambda_2(block_0));
      return Unit;
    };
  }
  function LanguageCard_init$lambda_0(this$LanguageCard) {
    return function () {
      this$LanguageCard.shown_0 = true;
      return Unit;
    };
  }
  function LanguageCard_init$lambda_1(this$LanguageCard) {
    return function () {
      this$LanguageCard.shown_0 = false;
      return Unit;
    };
  }
  LanguageCard.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'LanguageCard',
    interfaces: []
  };
  function SimplePresenter() {
    this.view_0 = new HtmlPage(this);
    this.matches_0 = LinkedHashMap_init();
    if (equals(navigator.clipboard, undefined)) {
      this.view_0.showCopyButton_6taknv$(false);
    }
  }
  Object.defineProperty(SimplePresenter.prototype, 'currentTextInput', {
    get: function () {
      return this.view_0.inputText;
    }
  });
  SimplePresenter.prototype.recognizeMatches_61zpoe$ = function (input) {
    if (input === void 0)
      input = this.currentTextInput;
    this.view_0.inputText = input;
    this.onInputChanges_61zpoe$(input);
    this.view_0.selectInputText();
  };
  function SimplePresenter$onButtonCopyClick$lambda(it) {
    window.alert('Could not copy text: ' + it);
    return Unit;
  }
  SimplePresenter.prototype.onButtonCopyClick = function () {
    navigator.clipboard.writeText(this.view_0.resultText).catch(SimplePresenter$onButtonCopyClick$lambda);
  };
  SimplePresenter.prototype.onButtonHelpClick = function () {
    this.view_0.showUserGuide_6taknv$(false);
  };
  SimplePresenter.prototype.showInitialUserGuide = function () {
    this.view_0.showUserGuide_6taknv$(true);
  };
  SimplePresenter.prototype.onInputChanges_61zpoe$ = function (newInput) {
    this.matches_0.clear();
    var tmp$ = this.matches_0;
    var $receiver = Recognizer$Companion_getInstance().recognize_mu52mh$(newInput);
    var destination = ArrayList_init_0(collectionSizeOrDefault($receiver, 10));
    var tmp$_0;
    tmp$_0 = $receiver.iterator();
    while (tmp$_0.hasNext()) {
      var item = tmp$_0.next();
      destination.add_11rb$(to(item, false));
    }
    tmp$.putAll_a2k3zr$(toMap(destination));
    this.view_0.displayText = newInput;
    this.view_0.showResults_oz4rjz$(this.matches_0.keys);
    this.computeOutputPattern_0();
  };
  SimplePresenter.prototype.onSuggestionClick_hdji9c$ = function (match) {
    var tmp$, tmp$_0;
    if (this.deactivatedMatches_0.contains_11rb$(match)) {
      return;
    }
    var $receiver = this.matches_0;
    var value = !((tmp$ = this.matches_0.get_11rb$(match)) != null ? tmp$ : false);
    $receiver.put_xwzc9p$(match, value);
    this.view_0.select_n54ctl$(match, (tmp$_0 = this.matches_0.get_11rb$(match)) != null ? tmp$_0 : false);
    var disabledMatches = this.deactivatedMatches_0;
    var tmp$_1;
    tmp$_1 = this.matches_0.keys.iterator();
    while (tmp$_1.hasNext()) {
      var element = tmp$_1.next();
      this.view_0.disable_n54ctl$(element, disabledMatches.contains_11rb$(element));
    }
    this.computeOutputPattern_0();
  };
  SimplePresenter.prototype.onOptionsChange_ow7xd4$ = function (options) {
    this.computeOutputPattern_0();
  };
  SimplePresenter.prototype.computeOutputPattern_0 = function () {
    var tmp$ = RecognizerCombiner$Companion_getInstance();
    var tmp$_0 = this.view_0.inputText;
    var $receiver = this.matches_0;
    var destination = LinkedHashMap_init();
    var tmp$_1;
    tmp$_1 = $receiver.entries.iterator();
    while (tmp$_1.hasNext()) {
      var element = tmp$_1.next();
      if (element.value) {
        destination.put_xwzc9p$(element.key, element.value);
      }
    }
    var destination_0 = ArrayList_init_0(destination.size);
    var tmp$_2;
    tmp$_2 = destination.entries.iterator();
    while (tmp$_2.hasNext()) {
      var item = tmp$_2.next();
      destination_0.add_11rb$(item.key);
    }
    var result = tmp$.combine_9yx9zl$(tmp$_0, toList_0(destination_0), this.view_0.options);
    this.view_0.resultText = result.pattern;
    this.view_0.showGeneratedCodeForPattern_61zpoe$(result.pattern);
  };
  Object.defineProperty(SimplePresenter.prototype, 'deactivatedMatches_0', {
    get: function () {
      var $receiver = this.matches_0;
      var destination = LinkedHashMap_init();
      var tmp$;
      tmp$ = $receiver.entries.iterator();
      while (tmp$.hasNext()) {
        var element = tmp$.next();
        if (element.value) {
          destination.put_xwzc9p$(element.key, element.value);
        }
      }
      var destination_0 = ArrayList_init_0(destination.size);
      var tmp$_0;
      tmp$_0 = destination.entries.iterator();
      while (tmp$_0.hasNext()) {
        var item = tmp$_0.next();
        destination_0.add_11rb$(item.key);
      }
      var selectedMatches = toList_0(destination_0);
      var $receiver_0 = this.matches_0.keys;
      var destination_1 = ArrayList_init();
      var tmp$_1;
      tmp$_1 = $receiver_0.iterator();
      while (tmp$_1.hasNext()) {
        var element_0 = tmp$_1.next();
        if (!selectedMatches.contains_11rb$(element_0))
          destination_1.add_11rb$(element_0);
      }
      var destination_2 = ArrayList_init();
      var tmp$_2;
      tmp$_2 = destination_1.iterator();
      loop_label: while (tmp$_2.hasNext()) {
        var element_1 = tmp$_2.next();
        var any$result;
        any$break: do {
          var tmp$_3;
          if (Kotlin.isType(selectedMatches, Collection) && selectedMatches.isEmpty()) {
            any$result = false;
            break any$break;
          }
          tmp$_3 = selectedMatches.iterator();
          while (tmp$_3.hasNext()) {
            var element_2 = tmp$_3.next();
            if (element_2.intersect_hdji9c$(element_1)) {
              any$result = true;
              break any$break;
            }
          }
          any$result = false;
        }
         while (false);
        if (any$result)
          destination_2.add_11rb$(element_1);
      }
      return toSet(destination_2);
    }
  });
  SimplePresenter.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SimplePresenter',
    interfaces: [DisplayContract$Presenter]
  };
  var package$org = _.org || (_.org = {});
  var package$olafneumann = package$org.olafneumann || (package$org.olafneumann = {});
  var package$regex = package$olafneumann.regex || (package$olafneumann.regex = {});
  var package$generator = package$regex.generator || (package$regex.generator = {});
  var package$js = package$generator.js || (package$generator.js = {});
  package$js.createStepDefinition_w74nik$ = createStepDefinition;
  package$js.jQuery_lt8gi4$ = jQuery;
  var package$regex_0 = package$generator.regex || (package$generator.regex = {});
  package$regex_0.BracketedRecognizer = BracketedRecognizer;
  Object.defineProperty(CodeGenerator, 'Companion', {
    get: CodeGenerator$Companion_getInstance
  });
  package$regex_0.CodeGenerator = CodeGenerator;
  package$regex_0.GeneratedSnippet = GeneratedSnippet;
  package$regex_0.SimpleReplacingCodeGenerator = SimpleReplacingCodeGenerator;
  package$regex_0.JavaCodeGenerator = JavaCodeGenerator;
  package$regex_0.KotlinCodeGenerator = KotlinCodeGenerator;
  package$regex_0.PhpCodeGenerator = PhpCodeGenerator;
  package$regex_0.JavaScriptCodeGenerator = JavaScriptCodeGenerator;
  package$regex_0.CSharpCodeGenerator = CSharpCodeGenerator;
  Object.defineProperty(Configuration, 'Companion', {
    get: Configuration$Companion_getInstance
  });
  package$regex_0.Configuration = Configuration;
  Object.defineProperty(Recognizer, 'Companion', {
    get: Recognizer$Companion_getInstance
  });
  package$regex_0.Recognizer = Recognizer;
  package$regex_0.RecognizerMatch = RecognizerMatch;
  Object.defineProperty(RecognizerCombiner, 'Companion', {
    get: RecognizerCombiner$Companion_getInstance
  });
  RecognizerCombiner.Options = RecognizerCombiner$Options;
  RecognizerCombiner.RegularExpression = RecognizerCombiner$RegularExpression;
  package$regex_0.RecognizerCombiner = RecognizerCombiner;
  package$regex_0.SimpleRecognizer = SimpleRecognizer;
  Object.defineProperty(package$generator, 'KEY_LAST_VERSION', {
    get: function () {
      return KEY_LAST_VERSION;
    }
  });
  Object.defineProperty(package$generator, 'KEY_LAST_VISIT', {
    get: function () {
      return KEY_LAST_VISIT;
    }
  });
  Object.defineProperty(package$generator, 'VAL_VERSION', {
    get: function () {
      return VAL_VERSION;
    }
  });
  Object.defineProperty(package$generator, 'VAL_EXAMPLE_INPUT', {
    get: function () {
      return VAL_EXAMPLE_INPUT;
    }
  });
  package$generator.main = main;
  DisplayContract.View = DisplayContract$View;
  DisplayContract.Presenter = DisplayContract$Presenter;
  var package$ui = package$generator.ui || (package$generator.ui = {});
  package$ui.DisplayContract = DisplayContract;
  Object.defineProperty(package$ui, 'ELEMENT_DIV', {
    get: function () {
      return ELEMENT_DIV;
    }
  });
  Object.defineProperty(package$ui, 'ELEMENT_BUTTON', {
    get: function () {
      return ELEMENT_BUTTON;
    }
  });
  Object.defineProperty(package$ui, 'ELEMENT_PRE', {
    get: function () {
      return ELEMENT_PRE;
    }
  });
  Object.defineProperty(package$ui, 'ELEMENT_CODE', {
    get: function () {
      return ELEMENT_CODE;
    }
  });
  Object.defineProperty(package$ui, 'HtmlHelper', {
    get: HtmlHelper_getInstance
  });
  Object.defineProperty(package$ui, 'CLASS_MATCH_ROW', {
    get: function () {
      return CLASS_MATCH_ROW;
    }
  });
  Object.defineProperty(package$ui, 'CLASS_MATCH_ITEM', {
    get: function () {
      return CLASS_MATCH_ITEM;
    }
  });
  Object.defineProperty(package$ui, 'CLASS_ITEM_SELECTED', {
    get: function () {
      return CLASS_ITEM_SELECTED;
    }
  });
  Object.defineProperty(package$ui, 'CLASS_ITEM_NOT_AVAILABLE', {
    get: function () {
      return CLASS_ITEM_NOT_AVAILABLE;
    }
  });
  Object.defineProperty(package$ui, 'EVENT_CLICK', {
    get: function () {
      return EVENT_CLICK;
    }
  });
  Object.defineProperty(package$ui, 'EVENT_INPUT', {
    get: function () {
      return EVENT_INPUT;
    }
  });
  Object.defineProperty(package$ui, 'ID_INPUT_ELEMENT', {
    get: function () {
      return ID_INPUT_ELEMENT;
    }
  });
  Object.defineProperty(package$ui, 'ID_TEXT_DISPLAY', {
    get: function () {
      return ID_TEXT_DISPLAY;
    }
  });
  Object.defineProperty(package$ui, 'ID_RESULT_DISPLAY', {
    get: function () {
      return ID_RESULT_DISPLAY;
    }
  });
  Object.defineProperty(package$ui, 'ID_ROW_CONTAINER', {
    get: function () {
      return ID_ROW_CONTAINER;
    }
  });
  Object.defineProperty(package$ui, 'ID_CONTAINER_INPUT', {
    get: function () {
      return ID_CONTAINER_INPUT;
    }
  });
  Object.defineProperty(package$ui, 'ID_CHECK_ONLY_MATCHES', {
    get: function () {
      return ID_CHECK_ONLY_MATCHES;
    }
  });
  Object.defineProperty(package$ui, 'ID_CHECK_CASE_INSENSITIVE', {
    get: function () {
      return ID_CHECK_CASE_INSENSITIVE;
    }
  });
  Object.defineProperty(package$ui, 'ID_CHECK_DOT_MATCHES_LINE_BRAKES', {
    get: function () {
      return ID_CHECK_DOT_MATCHES_LINE_BRAKES;
    }
  });
  Object.defineProperty(package$ui, 'ID_CHECK_MULTILINE', {
    get: function () {
      return ID_CHECK_MULTILINE;
    }
  });
  Object.defineProperty(package$ui, 'ID_BUTTON_COPY', {
    get: function () {
      return ID_BUTTON_COPY;
    }
  });
  Object.defineProperty(package$ui, 'ID_BUTTON_HELP', {
    get: function () {
      return ID_BUTTON_HELP;
    }
  });
  Object.defineProperty(package$ui, 'ID_DIV_LANGUAGES', {
    get: function () {
      return ID_DIV_LANGUAGES;
    }
  });
  package$ui.HtmlPage = HtmlPage;
  $$importsForInline$$['kotlinx-html-js'] = $module$kotlinx_html_js;
  package$ui.LanguageCard = LanguageCard;
  package$ui.SimplePresenter = SimplePresenter;
  Object.defineProperty(SimpleReplacingCodeGenerator.prototype, 'uniqueName', Object.getOwnPropertyDescriptor(CodeGenerator.prototype, 'uniqueName'));
  KEY_LAST_VERSION = 'user.lastVersion';
  KEY_LAST_VISIT = 'user.lastVisit';
  VAL_VERSION = 3;
  VAL_EXAMPLE_INPUT = "2020-03-12T13:34:56.123Z INFO  [org.example.Class]: This is a #simple #logline containing a 'value'.";
  ELEMENT_DIV = 'div';
  ELEMENT_BUTTON = 'button';
  ELEMENT_PRE = 'pre';
  ELEMENT_CODE = 'code';
  CLASS_MATCH_ROW = 'rg-match-row';
  CLASS_MATCH_ITEM = 'rg-match-item';
  CLASS_ITEM_SELECTED = 'rg-item-selected';
  CLASS_ITEM_NOT_AVAILABLE = 'rg-item-not-available';
  EVENT_CLICK = 'click';
  EVENT_INPUT = 'input';
  ID_INPUT_ELEMENT = 'rg_raw_input_text';
  ID_TEXT_DISPLAY = 'rg_text_display';
  ID_RESULT_DISPLAY = 'rg_result_display';
  ID_ROW_CONTAINER = 'rg_row_container';
  ID_CONTAINER_INPUT = 'rg_input_container';
  ID_CHECK_ONLY_MATCHES = 'rg_onlymatches';
  ID_CHECK_CASE_INSENSITIVE = 'rg_caseinsensitive';
  ID_CHECK_DOT_MATCHES_LINE_BRAKES = 'rg_dotmatcheslinebreakes';
  ID_CHECK_MULTILINE = 'rg_multiline';
  ID_BUTTON_COPY = 'rg_button_copy';
  ID_BUTTON_HELP = 'rg_button_show_help';
  ID_DIV_LANGUAGES = 'rg_language_accordion';
  main();
  Kotlin.defineModule('regex-generator-web', _);
  return _;
}(typeof this['regex-generator-web'] === 'undefined' ? {} : this['regex-generator-web'], kotlin, this['kotlinx-html-js']);

//# sourceMappingURL=regex-generator-web.js.map
