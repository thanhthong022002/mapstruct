/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.nullvaluepropertymapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.ap.testutil.IssueKey;
import org.mapstruct.ap.testutil.WithClasses;
import org.mapstruct.ap.testutil.compilation.annotation.CompilationResult;
import org.mapstruct.ap.testutil.compilation.annotation.Diagnostic;
import org.mapstruct.ap.testutil.compilation.annotation.ExpectedCompilationOutcome;
import org.mapstruct.ap.testutil.runner.AnnotationProcessorTestRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Sjaak Derksen
 */
@IssueKey("1306")
@RunWith(AnnotationProcessorTestRunner.class)
@WithClasses({
    Address.class,
    Customer.class,
    CustomerDTO.class,
    AddressDTO.class,
    HomeDTO.class,
    UserDTO.class
})
public class NullValuePropertyMappingTest {

    @Test
    @WithClasses(CustomerMapper.class)
    public void testStrategyAppliedOnForgedMethod() {

        Customer customer = new Customer();
        customer.setAddress( null );

        UserDTO userDTO = new UserDTO();
        userDTO.setHomeDTO( new HomeDTO() );
        userDTO.getHomeDTO().setAddressDTO( new AddressDTO() );
        userDTO.getHomeDTO().getAddressDTO().setHouseNo( 5 );
        userDTO.setDetails( Arrays.asList( "green hair" ) );

        CustomerMapper.INSTANCE.mapCustomer( customer, userDTO );

        assertThat( userDTO.getHomeDTO() ).isNotNull();
        assertThat( userDTO.getHomeDTO().getAddressDTO() ).isNotNull();
        assertThat( userDTO.getHomeDTO().getAddressDTO().getHouseNo() ).isEqualTo( 5 );
        assertThat( userDTO.getDetails() ).isNotNull();
        assertThat( userDTO.getDetails() ).containsExactly( "green hair" );
    }

    @Test
    @WithClasses({ NvpmsConfig.class, CustomerNvpmsOnConfigMapper.class })
    public void testHierarchyIgnoreOnConfig() {
        testConfigWhiteSpace( CustomerNvpmsOnConfigMapper.INSTANCE::map );
    }

    @Test
    @WithClasses(CustomerNvpmsOnMapperMapper.class)
    public void testHierarchyIgnoreOnMapping() {
        testConfigWhiteSpace( CustomerNvpmsOnMapperMapper.INSTANCE::map );
    }

    @Test
    @WithClasses(CustomerNvpmsOnBeanMappingMethodMapper.class)
    public void testHierarchyIgnoreOnBeanMappingMethod() {
        testConfigWhiteSpace( CustomerNvpmsOnBeanMappingMethodMapper.INSTANCE::map );
    }

    @Test
    @WithClasses(CustomerNvpmsPropertyMappingMapper.class)
    public void testHierarchyIgnoreOnPropertyMappingMethod() {
        testConfigWhiteSpace( CustomerNvpmsPropertyMappingMapper.INSTANCE::map );
    }

    @Test
    @WithClasses(CustomerDefaultMapper.class)
    public void testStrategyDefaultAppliedOnForgedMethod() {

        Customer customer = new Customer();
        customer.setAddress( null );

        UserDTO userDTO = new UserDTO();
        userDTO.setHomeDTO( new HomeDTO() );
        userDTO.getHomeDTO().setAddressDTO( new AddressDTO() );
        userDTO.getHomeDTO().getAddressDTO().setHouseNo( 5 );
        userDTO.setDetails( Arrays.asList( "green hair" ) );

        CustomerDefaultMapper.INSTANCE.mapCustomer( customer, userDTO );

        assertThat( userDTO.getHomeDTO() ).isNotNull();
        assertThat( userDTO.getHomeDTO().getAddressDTO() ).isNotNull();
        assertThat( userDTO.getHomeDTO().getAddressDTO().getHouseNo() ).isNull();
        assertThat( userDTO.getDetails() ).isNotNull();
        assertThat( userDTO.getDetails() ).isEmpty();
    }

    @Test
    @WithClasses(ErroneousCustomerMapper1.class)
    @ExpectedCompilationOutcome(
        value = CompilationResult.FAILED,
        diagnostics = {
            @Diagnostic(type = ErroneousCustomerMapper1.class,
                kind = javax.tools.Diagnostic.Kind.ERROR,
                line = 20,
                alternativeLine = 22, // Javac wrong error reporting on repeatable annotations JDK-8042710
                message = "Default value and nullValuePropertyMappingStrategy are both defined in @Mapping, " +
                    "either define a defaultValue or an nullValuePropertyMappingStrategy.")
        }
    )
    public void testBothDefaultValueAndNvpmsDefined() {
    }

    @Test
    @WithClasses(ErroneousCustomerMapper2.class)
    @ExpectedCompilationOutcome(
        value = CompilationResult.FAILED,
        diagnostics = {
            @Diagnostic(type = ErroneousCustomerMapper2.class,
                kind = javax.tools.Diagnostic.Kind.ERROR,
                line = 20,
                alternativeLine = 22, // Javac wrong error reporting on repeatable annotations JDK-8042710
                message = "Expression and nullValuePropertyMappingStrategy are both defined in @Mapping, " +
                    "either define an expression or an nullValuePropertyMappingStrategy.")
        }
    )
    public void testBothExpressionAndNvpmsDefined() {
    }

    @Test
    @WithClasses(ErroneousCustomerMapper3.class)
    @ExpectedCompilationOutcome(
        value = CompilationResult.FAILED,
        diagnostics = {
            @Diagnostic(type = ErroneousCustomerMapper3.class,
                kind = javax.tools.Diagnostic.Kind.ERROR,
                line = 20,
                alternativeLine = 22, // Javac wrong error reporting on repeatable annotations JDK-8042710
                message = "DefaultExpression and nullValuePropertyMappingStrategy are both defined in " +
                    "@Mapping, either define a defaultExpression or an nullValuePropertyMappingStrategy.")
        }
    )
    public void testBothDefaultExpressionAndNvpmsDefined() {
    }

    @Test
    @WithClasses(ErroneousCustomerMapper4.class)
    @ExpectedCompilationOutcome(
        value = CompilationResult.FAILED,
        diagnostics = {
            @Diagnostic(type = ErroneousCustomerMapper4.class,
                kind = javax.tools.Diagnostic.Kind.ERROR,
                line = 20,
                alternativeLine = 22, // Javac wrong error reporting on repeatable annotations JDK-8042710
                message = "Constant and nullValuePropertyMappingStrategy are both defined in @Mapping, " +
                    "either define a constant or an nullValuePropertyMappingStrategy.")
        }
    )
    public void testBothConstantAndNvpmsDefined() {
    }

    @Test
    @WithClasses(ErroneousCustomerMapper5.class)
    @ExpectedCompilationOutcome(
        value = CompilationResult.FAILED,
        diagnostics = {
            @Diagnostic(type = ErroneousCustomerMapper5.class,
                kind = javax.tools.Diagnostic.Kind.ERROR,
                line = 20,
                alternativeLine = 22, // Javac wrong error reporting on repeatable annotations JDK-8042710
                message = "Ignore and nullValuePropertyMappingStrategy are both defined in @Mapping, " +
                    "either define ignore or an nullValuePropertyMappingStrategy.")
        }
    )
    public void testBothIgnoreAndNvpmsDefined() {
    }

    private void testConfig(BiConsumer<Customer, CustomerDTO> customerMapper) {

        Customer customer = new Customer();
        customer.setAddress( null );

        CustomerDTO customerDto = new CustomerDTO();
        customerDto.setAddress( new AddressDTO() );
        customerDto.getAddress().setHouseNo( 5 );
        customerDto.setDetails( Arrays.asList( "green hair" ) );

        customerMapper.accept( customer, customerDto );

        assertThat( customerDto.getAddress() ).isNotNull();
        assertThat( customerDto.getAddress().getHouseNo() ).isEqualTo( 5 );
        assertThat( customerDto.getDetails() ).isNotNull();
        assertThat( customerDto.getDetails() ).containsExactly( "green hair" );
    }

    private void testConfigWhiteSpace(BiConsumer<Customer, CustomerDTO> customerMapper) {

        Customer customer = new Customer();
        customer.setAddress( null );
        customer.setFirstName("");
        customer.setLastName("");
        customer.setPhones(new ArrayList<>());

        CustomerDTO customerDto = new CustomerDTO();
        customerDto.setFirstName("Thong");
        customerDto.setLastName("Nguyen");
        customerDto.setAddress( new AddressDTO() );
        customerDto.getAddress().setHouseNo( 5 );
        customerDto.setDetails( Arrays.asList( "green hair" ) );

        List<String> phones = new ArrayList<>();
        phones.add("1");
        customerDto.setPhones( phones );

        customerMapper.accept( customer, customerDto );

        assertThat( customerDto.getFirstName() ).isEqualTo("Thong");
        assertThat( customerDto.getLastName() ).isEqualTo("");
        assertThat( customerDto.getAddress() ).isNotNull();
        assertThat( customerDto.getAddress().getHouseNo() ).isEqualTo( 5 );
        assertThat( customerDto.getDetails() ).isNotNull();
        assertThat( customerDto.getDetails() ).containsExactly( "green hair" );
        assertThat( customerDto.getPhones() ).containsExactly("1");
    }
}
