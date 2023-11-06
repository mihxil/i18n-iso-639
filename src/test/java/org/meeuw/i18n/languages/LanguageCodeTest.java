package org.meeuw.i18n.languages;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

@SuppressWarnings("OptionalGetWithoutIsPresent")
class LanguageCodeTest {
    
    @Test
    public void stream() {
        LanguageCode.stream().forEach(System.out::println);
    }
    
    @Test
    public void get() {
        assertThat(LanguageCode.getByCode("nld").get().getRefName()).isEqualTo("Dutch");
    }
    
    @Test
    public void getByPart1() {
        assertThat(LanguageCode.getByPart1("nl").get().getRefName()).isEqualTo("Dutch");
    }
    
    @Test
    public void getByPart2T() {
        assertThat(LanguageCode.getByPart2T("nld").get().getRefName()).isEqualTo("Dutch");
    }
    
    @Test
    public void getByPart2B() {
        assertThat(LanguageCode.getByPart2B("dut").get().getRefName()).isEqualTo("Dutch");
    }
    
    @Test
    public void getUnknown() {
        assertThat(LanguageCode.getByCode("doesntexist")).isEmpty();
    }
    
    

}