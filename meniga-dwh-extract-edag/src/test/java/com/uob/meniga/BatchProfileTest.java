package com.uob.meniga;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
public class BatchProfileTest {
    @Rule
    public OutputCapture outputCapture = new OutputCapture();

    @Test
    public void testUserProfileWiring() {
        testImpl("test,tables,users", "BatchJobConfig{outputFolder='target/MenigaExtractor/outputFiles/', outputFileName='users.csv', query='select id,name from users', sourceSystemCode='ADV', countryCode='TH', delimiter='\u0007', hashSumCol='id'}");
    }
    
    @Test
    public void testDimAccountTypesProfileWiring() {
        testImpl("test,tables,dim_account_types", "BatchJobConfig{outputFolder='target/MenigaExtractor/outputFiles/', outputFileName='dim_account_types.csv', query='select account_type_key, account_type_id,  account_type, account_class,  name, account_type_descr, import_native_account,  acct_org_id,  acct_org_bank_code_identifier,  acct_org_name,  acct_org_alt_name,  type_i_crc, insert_audit_key, update_audit_key, CONVERT(VARCHAR(10),xtract_date, 112), xtract_source from dim_account_types', sourceSystemCode='ADV', countryCode='TH', delimiter='\u0007', hashSumCol='account_type_id'}");
    }
    
    @Test 
    public void testDimAccountProfileWiring() {
    	testImpl("test,tables,dim_account","BatchJobConfig{outputFolder='target/MenigaExtractor/outputFiles/', outputFileName='dim_account.csv', query='select account_key,  account_id, account_identifier, name, balance,  limit,  CONVERT(VARCHAR(10),create_date, 112),  CONVERT(VARCHAR(10),last_update, 112),  inactive, inactive_descr, CONVERT(VARCHAR(10),attached_to_user_date, 112),  is_cloned_account,  is_cloned_account_descr,  currency_code,  is_demo_acct, is_wallet_acct, exclude_type, account_type_id,  account_type, account_type_class, account_type_name,  import_native_account,  acct_org_id,  acct_org_bank_code_identifier,  acct_org_name,  acct_org_alt_name,  primary_owner_id, account_churn_status, CONVERT(VARCHAR(10),churned_date, 112), type_i_crc, row_is_current, CONVERT(VARCHAR(10),row_start_date, 112), CONVERT(VARCHAR(10),row_end_date, 112), row_changed_reason, insert_audit_key, update_audit_key, CONVERT(VARCHAR(10),xtract_date, 112),  xtract_source,  user_db from dim_account', sourceSystemCode='ADV', countryCode='TH', delimiter='\u0007', hashSumCol='account_id'}");  
    }
    
    @Test
    public void testDimActivesProfileWiring() {
        testImpl("test,tables,dim_actives", "BatchJobConfig{outputFolder='target/MenigaExtractor/outputFiles/', outputFileName='dim_actives.csv', query='select active_key, active_status,  is_active_flag, insert_audit_key, update_audit_key, CONVERT(VARCHAR(10),xtract_date, 112),  xtract_source from dim_actives', sourceSystemCode='ADV', countryCode='TH', delimiter='\u0007', hashSumCol='active_key'}");
    }
    
    @Test
    public void testDimAdminUsersProfileWiring() {
        testImpl("test,tables,dim_admin_users", "BatchJobConfig{outputFolder='target/MenigaExtractor/outputFiles/', outputFileName='dim_admin_users.csv', query='select admin_user_key, admin_usr_id, native_user_identity, email,  CONVERT(VARCHAR(10),create_date, 112),  type_i_crc, insert_audit_key, update_audit_key, CONVERT(VARCHAR(10),xtract_date, 112),  xtract_source from dim_admin_users', sourceSystemCode='ADV', countryCode='TH', delimiter='\u0007', hashSumCol='admin_usr_id'}");
    }
    
    @Test
    public void testDimCategoriesProfileWiring() {
        testImpl("test,tables,dim_categories", "BatchJobConfig{outputFolder='target/MenigaExtractor/outputFiles/', outputFileName='dim_categories.csv', query='select category_key, category_id,  category_name,  sub_category_id,  sub_category_name,  category_type,  is_fixed_expense, classification, sub_classification, visibility, budget_generation_type, view_order, cash_flow_key,  type_i_crc, insert_audit_key, update_audit_key, CONVERT(VARCHAR(10),xtract_date, 112),  xtract_source from dim_categories', sourceSystemCode='ADV', countryCode='TH', delimiter='\u0007', hashSumCol='category_id'}");
    }
    
    @Test
    public void testDimHouseholdSizesProfileWiring() {
        testImpl("test,tables,dim_household_sizes", "BatchJobConfig{outputFolder='target/MenigaExtractor/outputFiles/', outputFileName='dim_household_sizes.csv', query='select household_size_key, household_size_id,  apartment_type_id,  apartment_type_descr, apartment_size, apartment_rooms,  number_in_family, number_of_kids, number_of_cars, apartment_size_n, apartment_rooms_n,  number_in_family_n, number_of_kids_n, number_of_cars_n, insert_audit_key, update_audit_key from dim_household_sizes', sourceSystemCode='ADV', countryCode='TH', delimiter='\u0007', hashSumCol='number_of_kids_n'}");
    }
    
    @Test
    public void testDimHouseholdsProfileWiring() {
        testImpl("test,tables,dim_households", "BatchJobConfig{outputFolder='target/MenigaExtractor/outputFiles/', outputFileName='dim_households.csv', query='select household_key,  household_id, household_id_h, household_size_id,  CONVERT(VARCHAR(10),create_date, 112),  CONVERT(VARCHAR(10),last_login_date, 112),  apartment_type_id,  apartment_type_descr,   apartment_rooms,  apartment_size_n, apartment_rooms_n,  postal_code,  number_in_family, number_of_kids, number_of_cars, number_in_family_n, number_of_kids_n, number_of_cars_n, realm_cnt,  person_cnt, shared_account_status,  complete_household,   marital_status_descr, account_cnt,  credit_card_cnt,  current_account_cnt,  savings_account_cnt,    demo_account_cnt, has_account_flag, has_account_descr,  household_churn_status, household_type, is_significant, has_valid_demographics,   comparison_activity_kpi,  transaction_stability_kpi,  eop_tv, eop_tv_other, card_check, CONVERT(VARCHAR(10),churned_date, 112), currency_code,  type_i_crc, row_is_current, CONVERT(VARCHAR(10),row_start_date, 112), CONVERT(VARCHAR(10),row_end_date, 112), row_changed_reason, insert_audit_key, update_audit_key, CONVERT(VARCHAR(10),xtract_date, 112),  xtract_source,  py_eop_tv,  py_eop_tv_other from dim_households', sourceSystemCode='ADV', countryCode='TH', delimiter='\u0007', hashSumCol='account_cnt'}");
    }
    
    @Test
    public void testDimLifeGoalsProfileWiring() {
        testImpl("test,tables,dim_life_goals", "BatchJobConfig{outputFolder='target/MenigaExtractor/outputFiles/', outputFileName='dim_life_goals.csv', query='select life_goal_key,  life_goal_id, CONVERT(VARCHAR(10),create_date, 112),  CONVERT(VARCHAR(10),modified_date, 112),  CONVERT(VARCHAR(10),start_date, 112), CONVERT(VARCHAR(10),target_date, 112),  life_goal_name, target_amount,  recurring_amount, intercept_amount, current_amount, is_deleted, is_deleted_descr, is_achieved,  is_achieved_descr,  CONVERT(VARCHAR(10),achieved_date, 112),  is_withdrawn, is_withdrawn_descr, category_id,  category_name,  type_i_crc, insert_audit_key, update_audit_key, CONVERT(VARCHAR(10),xtract_date, 112),  xtract_source,  user_db from dim_life_goals', sourceSystemCode='ADV', countryCode='TH', delimiter='\u0007', hashSumCol='target_amount'}");
    }
    
    @Test
    public void testDimNotificationChannelsProfileWiring() {
        testImpl("test,tables,dim_notification_channels", "BatchJobConfig{outputFolder='target/MenigaExtractor/outputFiles/', outputFileName='dim_notification_channels.csv', query='select notification_channel_key, notification_channel_id,  channel_name, insert_audit_key, update_audit_key, CONVERT(VARCHAR(10),xtract_date, 112),  xtract_source from dim_notification_channels', sourceSystemCode='ADV', countryCode='TH', delimiter='\u0007', hashSumCol='notification_channel_id'}");
    }
    


    private void testImpl(String profiles, String expectedProcessor) {
        System.setProperty("spring.profiles.active", profiles);
        MenigaExtractorApplication.main(new String[]{"Testing"});
        String output = this.outputCapture.toString();
        assertThat(output).contains(expectedProcessor);
    }

}
