package common;

import models.*;

/**
 *
 * @author johan
 */
public class Timings {

	public static double[] getExtremes(long stageID, long weaponCategoryID) {
		double[] result = new double[]{0.0, 0.0};

		Stage stage = Stage.findById(stageID);
		WeaponCategory weaponCategory = WeaponCategory.findById(weaponCategoryID);
		if (stage != null && weaponCategory != null) {
			Double minTime = null;
			Double maxTime = null;
			double targetTime = 0;
			int targets = 0;
			int movements = 0;

			try {
				for (TargetGroup targetGroup : stage.targetGroups) {
					for (Target target : targetGroup.targets) {
						String classification = String.format("%d", target.targetShape.classification);
						TargetClass targetClass = TargetClass.find("byClassificationAndWeaponCategory", classification, weaponCategory).first();
						if (targetClass != null) {
							double time = weaponCategory.shotInterval * ((double) targetGroup.range / (double) targetClass.maximumRange);
							if (minTime == null) {
								minTime = time;
							} else {
								minTime = Math.min(time, minTime);

							}
							if (maxTime == null) {
								maxTime = time;
							} else {
								maxTime = Math.max(time, maxTime);
							}
							targetTime += time;
							targets++;
						}
					}
					movements++;
				}
				if (stage.startingPosition != null && !stage.startingPosition.aimingAllowed) {
					movements++;
				}

				if (minTime != null && maxTime != null) {
					minTime = targetTime + (minTime * (6 - targets)) + 1.0 * (movements - 1);
					maxTime = targetTime + (maxTime * (6 - targets)) + 1.5 * (movements - 1);

					result = new double[]{minTime, maxTime};
				}

			} catch (Exception e) {
			}
		}
		return result;
	}
}