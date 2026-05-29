package com.anix.app.data.repositories

import com.anix.app.core.network.ApiService
import com.anix.app.data.models.*

class ClanRepository(private val api: ApiService) {

    suspend fun getClans(page: Int = 1, limit: Int = 20): Result<List<Clan>> {
        return try {
            val response = api.getClans(page, limit)
            val body = response.body()
            if (response.isSuccessful && body?.success == true) {
                Result.success(body.data ?: emptyList())
            } else {
                Result.failure(Exception(body?.error ?: "Failed to fetch clans"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createClan(name: String, tag: String, description: String = ""): Result<Clan> {
        return try {
            val response = api.createClan(CreateClanRequest(name, tag, description))
            val body = response.body()
            if (response.isSuccessful && body?.success == true && body.data != null) {
                Result.success(body.data)
            } else {
                Result.failure(Exception(body?.error ?: "Failed to create clan"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getClanDetail(id: String): Result<Clan> {
        return try {
            val response = api.getClanDetail(id)
            val body = response.body()
            if (response.isSuccessful && body?.success == true && body.data != null) {
                Result.success(body.data)
            } else {
                Result.failure(Exception(body?.error ?: "Failed to fetch clan"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getClanMembers(id: String): Result<List<ClanMember>> {
        return try {
            val response = api.getClanMembers(id)
            val body = response.body()
            if (response.isSuccessful && body?.success == true) {
                Result.success(body.data ?: emptyList())
            } else {
                Result.failure(Exception(body?.error ?: "Failed to fetch members"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun joinClan(id: String): Result<Unit> {
        return try {
            val response = api.joinClan(id)
            val body = response.body()
            if (response.isSuccessful && body?.success == true) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(body?.error ?: "Failed to join clan"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun kickMember(clanId: String, memberId: String): Result<Unit> {
        return try {
            val response = api.kickMember(clanId, memberId)
            val body = response.body()
            if (response.isSuccessful && body?.success == true) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(body?.error ?: "Failed to kick member"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateMemberRole(clanId: String, memberId: String, role: String): Result<Unit> {
        return try {
            val response = api.updateMemberRole(clanId, memberId, UpdateRoleRequest(memberId, role))
            val body = response.body()
            if (response.isSuccessful && body?.success == true) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(body?.error ?: "Failed to update role"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun donateToClan(clanId: String, amount: Int): Result<ClanWallet> {
        return try {
            val response = api.donateToClan(clanId, DonateRequest(amount))
            val body = response.body()
            if (response.isSuccessful && body?.success == true && body.data != null) {
                Result.success(body.data)
            } else {
                Result.failure(Exception(body?.error ?: "Failed to donate"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun boostClan(clanId: String): Result<Unit> {
        return try {
            val response = api.boostClan(clanId)
            val body = response.body()
            if (response.isSuccessful && body?.success == true) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(body?.error ?: "Failed to boost clan"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getClanWallet(clanId: String): Result<ClanWallet> {
        return try {
            val response = api.getClanWallet(clanId)
            val body = response.body()
            if (response.isSuccessful && body?.success == true && body.data != null) {
                Result.success(body.data)
            } else {
                Result.failure(Exception(body?.error ?: "Failed to fetch wallet"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUpgradeCatalog(): Result<List<ClanUpgrade>> {
        return try {
            val response = api.getUpgradeCatalog()
            val body = response.body()
            if (response.isSuccessful && body?.success == true) {
                Result.success(body.data ?: emptyList())
            } else {
                Result.failure(Exception(body?.error ?: "Failed to fetch upgrades"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun purchaseUpgrade(clanId: String, upgradeId: String): Result<ClanUpgrade> {
        return try {
            val response = api.purchaseUpgrade(clanId, PurchaseUpgradeRequest(upgradeId))
            val body = response.body()
            if (response.isSuccessful && body?.success == true && body.data != null) {
                Result.success(body.data)
            } else {
                Result.failure(Exception(body?.error ?: "Failed to purchase upgrade"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getClanLeaderboard(page: Int = 1, limit: Int = 20): Result<List<Clan>> {
        return try {
            val response = api.getClanLeaderboard(page, limit)
            val body = response.body()
            if (response.isSuccessful && body?.success == true) {
                Result.success(body.data ?: emptyList())
            } else {
                Result.failure(Exception(body?.error ?: "Failed to fetch leaderboard"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMyClan(): Result<Clan> {
        return try {
            val response = api.getMyClan()
            val body = response.body()
            if (response.isSuccessful && body?.success == true && body.data != null) {
                Result.success(body.data)
            } else {
                Result.failure(Exception(body?.error ?: "Not in a clan"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}