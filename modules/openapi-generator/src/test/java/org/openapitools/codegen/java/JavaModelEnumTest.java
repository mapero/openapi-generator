/*
 * Copyright 2018 OpenAPI-Generator Contributors (https://openapi-generator.tech)
 * Copyright 2018 SmartBear Software
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openapitools.codegen.java;

import io.swagger.v3.oas.models.media.*;

import org.apache.commons.lang3.StringUtils;
import org.openapitools.codegen.CodegenModel;
import org.openapitools.codegen.CodegenProperty;
import org.openapitools.codegen.DefaultCodegen;
import org.openapitools.codegen.languages.JavaClientCodegen;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class JavaModelEnumTest {
    @Test(description = "convert a java model with an enum")
    public void converterTest() {
        final StringSchema enumSchema = new StringSchema();
        enumSchema.setEnum(Arrays.asList("VALUE1", "VALUE2", "VALUE3"));
        final Schema model = new Schema().type("object").addProperties("name", enumSchema);

        final JavaClientCodegen codegen = new JavaClientCodegen();
        final CodegenModel cm = codegen.fromModel("sample", model, Collections.singletonMap("sample", model));

        Assert.assertEquals(cm.vars.size(), 1);

        final CodegenProperty enumVar = cm.vars.get(0);
        Assert.assertEquals(enumVar.baseName, "name");
        Assert.assertEquals(enumVar.dataType, "String");
        Assert.assertEquals(enumVar.datatypeWithEnum, "NameEnum");
        Assert.assertEquals(enumVar.name, "name");
        Assert.assertEquals(enumVar.defaultValue, null);
        Assert.assertEquals(enumVar.baseType, "String");
        Assert.assertTrue(enumVar.isEnum);
    }

    @Test(description = "convert a java model with an enum inside a list")
    public void converterInArrayTest() {
        final ArraySchema enumSchema = new ArraySchema().items(
                new StringSchema().addEnumItem("Aaaa").addEnumItem("Bbbb"));
        final Schema model = new Schema().type("object").addProperties("name", enumSchema);

        final DefaultCodegen codegen = new JavaClientCodegen();
        final CodegenModel cm = codegen.fromModel("sample", model, Collections.singletonMap("sample", model));

        Assert.assertEquals(cm.vars.size(), 1);

        final CodegenProperty enumVar = cm.vars.get(0);
        Assert.assertEquals(enumVar.baseName, "name");
        Assert.assertEquals(enumVar.dataType, "List<String>");
        Assert.assertEquals(enumVar.datatypeWithEnum, "List<NameEnum>");
        Assert.assertEquals(enumVar.name, "name");
        Assert.assertEquals(enumVar.defaultValue, "new ArrayList<NameEnum>()");
        Assert.assertEquals(enumVar.baseType, "List");
        Assert.assertTrue(enumVar.isEnum);

        Assert.assertEquals(enumVar.mostInnerItems.baseName, "name");
        Assert.assertEquals(enumVar.mostInnerItems.dataType, "String");
        Assert.assertEquals(enumVar.mostInnerItems.datatypeWithEnum, "NameEnum");
        Assert.assertEquals(enumVar.mostInnerItems.name, "name");
        Assert.assertEquals(enumVar.mostInnerItems.defaultValue, null);
        Assert.assertEquals(enumVar.mostInnerItems.baseType, "String");

        Assert.assertEquals(enumVar.mostInnerItems.baseType, enumVar.items.baseType);
    }

    @Test(description = "convert a java model with an enum inside a list")
    public void converterInArrayInArrayTest() {
        final ArraySchema enumSchema = new ArraySchema().items(
                new ArraySchema().items(
                        new StringSchema().addEnumItem("Aaaa").addEnumItem("Bbbb")));
        final Schema model = new Schema().type("object").addProperties("name", enumSchema);

        final DefaultCodegen codegen = new JavaClientCodegen();
        final CodegenModel cm = codegen.fromModel("sample", model, Collections.singletonMap("sample", model));

        Assert.assertEquals(cm.vars.size(), 1);

        final CodegenProperty enumVar = cm.vars.get(0);
        Assert.assertEquals(enumVar.baseName, "name");
        Assert.assertEquals(enumVar.dataType, "List<List<String>>");
        Assert.assertEquals(enumVar.datatypeWithEnum, "List<List<NameEnum>>");
        Assert.assertEquals(enumVar.name, "name");
        Assert.assertEquals(enumVar.defaultValue, "new ArrayList<List<NameEnum>>()");
        Assert.assertEquals(enumVar.baseType, "List");
        Assert.assertTrue(enumVar.isEnum);

        Assert.assertEquals(enumVar.mostInnerItems.baseName, "name");
        Assert.assertEquals(enumVar.mostInnerItems.dataType, "String");
        Assert.assertEquals(enumVar.mostInnerItems.datatypeWithEnum, "NameEnum");
        Assert.assertEquals(enumVar.mostInnerItems.name, "name");
        Assert.assertEquals(enumVar.mostInnerItems.defaultValue, null);
        Assert.assertEquals(enumVar.mostInnerItems.baseType, "String");

        Assert.assertEquals(enumVar.mostInnerItems.baseType, enumVar.items.items.baseType);
    }

    @Test(description = "not override identical parent enums")
    public void overrideEnumTest() {
        final StringSchema identicalEnumProperty = new StringSchema();
        identicalEnumProperty.setEnum(Arrays.asList("VALUE1", "VALUE2", "VALUE3"));

        final StringSchema subEnumProperty = new StringSchema();
        subEnumProperty.setEnum(Arrays.asList("SUB1", "SUB2", "SUB3"));

        // Add one enum property to the parent
        final Map<String, Schema> parentProperties = new HashMap<>();
        parentProperties.put("sharedThing", identicalEnumProperty);

        // Add TWO enums to the subType model; one of which is identical to the one in parent class
        final Map<String, Schema> subProperties = new HashMap<>();
        subProperties.put("unsharedThing", subEnumProperty);

        final Schema parentModel = new Schema();
        parentModel.setProperties(parentProperties);
        parentModel.name("parentModel");

        Discriminator discriminator = new Discriminator().mapping("name", StringUtils.EMPTY);
        discriminator.setPropertyName("model_type");
        parentModel.setDiscriminator(discriminator);

        final ComposedSchema composedSchema = new ComposedSchema()
                .addAllOfItem(new Schema().$ref(parentModel.getName()));

        final JavaClientCodegen codegen = new JavaClientCodegen();
        final Map<String, Schema> allModels = new HashMap<String, Schema>();
        allModels.put(parentModel.getName(), parentModel);
        allModels.put(composedSchema.getName(), composedSchema);

        final CodegenModel cm = codegen.fromModel("sample", composedSchema, allModels);

        Assert.assertEquals(cm.name, "sample");
        Assert.assertEquals(cm.classname, "Sample");
        Assert.assertEquals(cm.parent, "ParentModel");
        Assert.assertTrue(cm.imports.contains("ParentModel"));
    }
}
