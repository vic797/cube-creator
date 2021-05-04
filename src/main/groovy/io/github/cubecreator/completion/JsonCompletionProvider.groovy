package io.github.cubecreator.completion

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import org.fife.ui.autocomplete.BasicCompletion
import org.fife.ui.autocomplete.DefaultCompletionProvider
import org.fife.ui.autocomplete.FunctionCompletion
import org.fife.ui.autocomplete.MarkupTagCompletion
import org.fife.ui.autocomplete.ParameterizedCompletion
import org.fife.ui.autocomplete.ShorthandCompletion
import org.fife.ui.autocomplete.TemplateCompletion
import org.fife.ui.autocomplete.VariableCompletion

import java.nio.charset.StandardCharsets

class JsonCompletionProvider extends DefaultCompletionProvider {

    private final Parser parser
    private final HtmlRenderer renderer

    JsonCompletionProvider() {
        parser = Parser.builder().build()
        renderer = HtmlRenderer.builder().build()
    }

    void loadFromJson(InputStream stream) throws IOException {
        loadFromJson(IOUtils.toString(stream, StandardCharsets.UTF_8))
    }

    void loadFromJson(File file) throws IOException {
        loadFromJson(FileUtils.readFileToString(file, StandardCharsets.UTF_8))
    }

    void loadFromJson(URL url) throws IOException {
        loadFromJson(IOUtils.toString(url, StandardCharsets.UTF_8))
    }

    void loadFromJson(String json) throws IOException {
        JsonObject object = JsonParser.parseString(json).getAsJsonObject()
        if (object.has("param_start") && object.has("param_end") && object.has("param_separator")) {
            char paramStart = object.get("param_start").getAsString().charAt(0)
            char paramEnd = object.get("param_end").getAsString().charAt(0)
            String separator = object.get("param_separator").getAsString()
            setParameterizedCompletionParams(paramStart, separator, paramEnd)
        }
        if (object.has("completions")) {
            JsonArray completions = object.get("completions").getAsJsonArray()
            for (int i = 0; i < completions.size(); i++) {
                JsonObject completion = completions.get(i).getAsJsonObject()
                if (completion.has("type")) {
                    String type = completion.get("type").getAsString()
                    switch (type) {
                        case "function": {
                            addFunctionCompletion(completion)
                            break
                        }
                        case "constant": {
                            addConstantCompletion(completion)
                            break
                        }
                        case "tag": {
                            addTagCompletion(completion)
                            break
                        }
                        case "shorthand": {
                            addShorthandCompletion(completion)
                            break
                        }
                        case "template": {
                            addTemplateCompletion(completion)
                            break
                        }
                        case "variable": {
                            addVariableCompletion(completion)
                            break
                        }
                        case "other": {
                            addOtherCompletion(completion)
                            break
                        }
                    }
                }
            }
        }
    }

    @Deprecated
    @Override
    void loadFromXML(InputStream inputStream) throws IOException {}

    @Deprecated
    @Override
    void loadFromXML(File file) throws IOException {}

    @Deprecated
    @Override
    void loadFromXML(InputStream inputStream, ClassLoader cl) throws IOException {}

    @Deprecated
    @Override
    void loadFromXML(String resource) throws IOException {}

    private void addFunctionCompletion(JsonObject object) {
        if (object.has("name")) {
            String name = object.get("name").getAsString()
            String type = object.has("return_type") ? object.get("return_type").getAsString() : ""
            FunctionCompletion completion = new FunctionCompletion(this, name, type)
            if (object.has("defined_in")) {
                completion.definedIn = object.get("defined_in").getAsString()
            }
            if (object.has("description")) {
                completion.shortDescription = renderer.render(parser.parse(object.get("description").getAsString()))
            }
            if (object.has("return_value_desc")) {
                completion.returnValueDescription = object.get("return_value_desc").getAsString()
            }
            if (object.has("params")) {
                JsonArray params = object.get("params").getAsJsonArray()
                List<ParameterizedCompletion.Parameter> parameterList = new ArrayList<>()
                for (int i = 0; i < params.size(); i++) {
                    JsonObject param = params.get(i).getAsJsonObject()
                    if (param.has("type") && param.has("name")) {
                        ParameterizedCompletion.Parameter parameter
                        if (param.has("end_param")) {
                            parameter = new ParameterizedCompletion.Parameter(param.get("type").getAsString(), param.get("name").getAsString(), param.get("end_param").getAsBoolean())
                        } else {
                            parameter = new ParameterizedCompletion.Parameter(param.get("type").getAsString(), param.get("name").getAsString())
                        }
                        if (param.has("description")) {
                            parameter.description = renderer.render(parser.parse(param.get("description").getAsString()))
                        }
                        parameterList.add(parameter)
                    }
                    completion.params = parameterList
                }
            }
            checkProviderAndAdd(completion)
        }
    }

    private void addConstantCompletion(JsonObject object) {
        addVariableCompletion(object)
    }

    private void addTagCompletion(JsonObject object) {
        if (object.has("name")) {
            MarkupTagCompletion completion = new MarkupTagCompletion(this, object.get("name").getAsString())
            if (object.has("attributes")) {
                JsonArray params = object.get("attributes").getAsJsonArray()
                List<ParameterizedCompletion.Parameter> parameterList = new ArrayList<>()
                for (int i = 0; i < params.size(); i++) {
                    JsonObject param = params.get(i).getAsJsonObject()
                    if (param.has("type") && param.has("name")) {
                        ParameterizedCompletion.Parameter parameter = new ParameterizedCompletion.Parameter(param.get("type").getAsString(), param.get("name").getAsString())
                        if (param.has("description")) {
                            parameter.description = renderer.render(parser.parse(param.get("description").getAsString()))
                        }
                        parameterList.add(parameter)
                    }
                    completion.attributes = parameterList
                }
            }
            checkProviderAndAdd(completion)
        }
    }

    private void addShorthandCompletion(JsonObject object) {
        if (object.has("input_text") && object.has("replacement_text")) {
            String inputText = object.get("input_text").getAsString()
            String replacementText = object.get("replacement_text").getAsString()
            ShorthandCompletion completion = new ShorthandCompletion(this, inputText, replacementText)
            if (object.has("description")) {
                completion.shortDescription = renderer.render(parser.parse(object.get("description").getAsString()))
            }
            if (object.has("summary")) {
                String summary = object.get("summary").getAsString()
                completion.summary = renderer.render(parser.parse(summary))
            }
        }
    }

    private void addTemplateCompletion(JsonObject object) {
        if (object.has("input") && object.has("definition") && object.has("template")) {
            TemplateCompletion completion
            if (object.has("summary") && object.has("description")) {
                completion = new TemplateCompletion(this, object.get("input").getAsString(),
                        object.get("definition").getAsString(),
                        object.get("template").getAsString(),
                        renderer.render(parser.parse(object.get("description").getAsString())),
                        renderer.render(parser.parse(object.get("summary").getAsString())))
            } else {
                completion = new TemplateCompletion(this, object.get("input").getAsString(), object.get("definition").getAsString(), object.get("template").getAsString())
            }
            checkProviderAndAdd(completion)
        }
    }

    private void addVariableCompletion(JsonObject object) {
        if (object.has("name") && object.has("variable_type")) {
            String name = object.get("name").getAsString()
            String type = object.get("variable_type").getAsString()
            VariableCompletion completion = new VariableCompletion(this, name, type)
            if (object.has("defined_in")) {
                completion.definedIn = object.get("defined_in").getAsString()
            }
            checkProviderAndAdd(completion)
        }
    }

    private void addOtherCompletion(JsonObject object) {
        if (object.has("replacement")) {
            BasicCompletion completion = new BasicCompletion(this, object.get("replacement").getAsString())
            if (object.has("description")) {
                completion.shortDescription = renderer.render(parser.parse(object.get("description").getAsString()))
            }
            if (object.has("summary")) {
                completion.summary = object.get("summary").getAsString()
            }
            checkProviderAndAdd(completion)
        }
    }
}
