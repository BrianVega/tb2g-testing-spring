package org.springframework.samples.petclinic.web;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.reset;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringJUnitWebConfig(locations = {"classpath:spring/mvc-test-config.xml", "classpath:spring/mvc-core-config.xml"})
class OwnerControllerTest {

    @Autowired
    OwnerController ownerController;

    @Autowired
    ClinicService clinicService;

    MockMvc mockMvc;


    @Test
    void tempTest() {

        //ownerController
    }

    @BeforeEach
    void setup() {
        // It's a "standalone" setup, so it doesn't require an actual running server or a full Spring application context, which makes it faster for unit testing.
        mockMvc = MockMvcBuilders.standaloneSetup(ownerController).build();
    }

    @AfterEach
    void tearDown() {
      reset(clinicService);
    }

    @Test
    void testNewOwnerPostValid() throws Exception {
        mockMvc.perform(post("/owners/new")
                .param("firstName", "John")
                .param("lastName", "Doe")
                .param("Address", "123 Main St")
                .param("city", "London")
                .param("telephone", "9999999999"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void testNewOwnerPostInvalid() throws Exception {
        mockMvc.perform(post("/owners/new")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("city", "London"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors("owner"))
                .andExpect(model().attributeHasFieldErrors("owner", "address"))
                .andExpect(model().attributeHasFieldErrors("owner", "telephone"))
                .andExpect(view().name("owners/createOrUpdateOwnerForm"));
    }


    @Test
    void testFindByNameNotFound() throws Exception {
        mockMvc.perform(get("/owners")
                .param("lastName", "test"))
                .andExpect(status().isOk())
                .andExpect(view().name("owners/findOwners"));
    }

    @Test
    void testProcessFindFormParameterless() throws Exception {
        mockMvc.perform(get("/owners"))
                .andExpect(status().isOk())
                .andExpect(view().name("owners/findOwners"))
                .andExpect(model().attributeExists("owner"));
    }

    @Test
    void testReturnListOfOwners() throws Exception {
        given(clinicService.findOwnerByLastName("")).willReturn(Lists.newArrayList(new Owner(), new Owner()));

        mockMvc.perform(get("/owners"))
                .andExpect(status().isOk())
                .andExpect(view().name("owners/ownersList"));
    }

    @Test
    void testFindOwnerOneResult() throws Exception {
        Owner owner = new Owner();
        owner.setId(1);


        given(clinicService.findOwnerByLastName(anyString())).willReturn(Lists.newArrayList(owner));

        mockMvc.perform(get("/owners"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/owners/1"));
    }


    @Test
    void initCreationForm() throws Exception {
        mockMvc.perform(get("/owners/new"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("owner"))
                .andExpect(view().name("owners/createOrUpdateOwnerForm"));
    }
}