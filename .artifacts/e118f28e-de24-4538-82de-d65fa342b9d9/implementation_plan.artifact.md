# Implementation Plan - Custom Categories

Add the ability for users to create their own bill categories with custom names, icons, and colors.

## Proposed Changes

### UI Layer

#### [MODIFY] [AddEditBillViewModel.kt](file:///C:/androifff/app/src/main/java/com/example/myapplicationds/ui/addedit/AddEditBillViewModel.kt)
- Add `addNewCategory(name: String, icon: String, color: Long)` to the ViewModel.
- This function will:
    - Insert a new `CategoryEntity` into the database via `BillRepository`.
    - Automatically update the current bill's category state to use the new category.

#### [MODIFY] [AddEditBillScreen.kt](file:///C:/androifff/app/src/main/java/com/example/myapplicationds/ui/addedit/AddEditBillScreen.kt)
- Implement a `NewCategoryDialog` to collect:
    - Category Name (Text)
    - Icon (Selected from the existing `availableIcons` list)
    - Color (Selected from the existing `CategoryColors` list)
- Add a special "Add New Category..." item at the end of the category dropdown list.
- When selected, show the `NewCategoryDialog`.

## Verification Plan

### Manual Verification
1. Navigate to the **Add Bill** screen.
2. Tap the **Category** dropdown.
3. Scroll to the bottom and tap **Add New Category...**.
4. Fill in the details in the dialog and tap **Add**.
5. Confirm that the new category is now selected and visible in the dropdown.
6. Save the bill and ensure it shows the correct custom category on the Home screen.
